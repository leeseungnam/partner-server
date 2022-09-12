package kr.wrightbrothers.apps.file.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import kr.wrightbrothers.apps.common.util.ImageConverter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
public class S3Service {
    
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void initialize() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

   /**
    * 썸네일 이미지 생성 후 WAS 업로드
    *
    * @param file 파일
    * @param baseDir 업로드 경로
    * @return 이미지 URL
    */
    public String uploadThumbImage(File file, String baseDir) throws IOException {
        String randName = UUID.randomUUID().toString();
        String fileName = baseDir + "/" + randName + ".jpg";
        String fileName1 =  baseDir + "/" + "thumb1_" + randName + ".jpg"; // 120
        String fileName2 =  baseDir + "/" + "thumb2_" + randName + ".jpg"; // 270
        String fileName3 =  baseDir + "/" + "thumb3_" + randName + ".jpg"; // 730

        // 파일 업로드
        uploadS3File(file, bucket, fileName);
        // 120 이미지 업로드
        uploadS3ThumbImage(file, bucket, 120, fileName1);
        // 270 이미지 업로드
        uploadS3ThumbImage(file, bucket, 270, fileName2);
        // 730 이미지 업로드
        uploadS3ThumbImage(file, bucket, 730, fileName3);

        return s3Client.getUrl(bucket, fileName).toString();
    }

    public void uploadS3File(File file, String bucketName, String fileName) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        byte[] bytes = Files.readAllBytes(file.toPath());
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public void uploadS3ThumbImage(File file, String bucketName, int imageSize, String fileName) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        InputStream inputStream = ImageConverter.imageConverter(file, imageSize, true);
        byte[] bytes = IOUtils.toByteArray(Objects.requireNonNull(inputStream));
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
    }

    /**
     * 일반 파일 업로드
     *
     * @param file 파일
     * @param baseDir 업로드 경로
     * @return 파일 URL
     */
    public String uploadFile(File file, String baseDir) throws IOException {
        String fileName = baseDir + "/" + UUID.randomUUID();
        // 파일 업로드
        uploadS3File(file, bucket, fileName);

        return s3Client.getUrl(bucket, fileName).toString();
    }


    /**
     * 에디터 파일 업로드
     *
     * @param bytes 에디터 업로드 바이트 정보
     * @param baseDir 업로드 경로
     * @return 파일 URL
     */
    public String editorUploadFile(byte[] bytes, String baseDir) {
        String fileName = baseDir + UUID.randomUUID();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        s3Client.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString();
    }


    /**
     * 파일 삭제
     *
     * @param a3Key 파일 저장 키
     */
    public void fileDelete(String a3Key) {
    	 DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, a3Key);
    	 s3Client.deleteObject(deleteObjectRequest);
    }
    
    /**
     * 폴더 전체 삭제
     *
     * @param a3Key 파일 저장 폴더
     */
	public void folderDelete(String a3Key) {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket).withPrefix(a3Key);
		ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
        objectListing.getObjectSummaries().forEach(s3ObjectSummary -> fileDelete(s3ObjectSummary.getKey()));
	}

}