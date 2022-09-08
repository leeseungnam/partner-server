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
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
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
    * @param file
    * @param baseDir
    * @return
    * @throws IOException
    */
    public String uploadThumbImage(File file, String baseDir) throws IOException {
        String randName = UUID.randomUUID().toString();
        String fileName = baseDir + "/" + randName + ".jpg";
        String fileName1 =  baseDir + "/" + "thumb1_" + randName + ".jpg"; // 120
        String fileName2 =  baseDir + "/" + "thumb2_" + randName + ".jpg"; // 270
        String fileName3 =  baseDir + "/" + "thumb3_" + randName + ".jpg"; // 730
        // thumb1_ => 120, thumb2_ => 270, thumb3_ => 730    
        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] bytes = Files.readAllBytes(file.toPath());
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        s3Client.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        ////////////////////////////////////////////////////////////////////////
        // 730 사이즈
        ////////////////////////////////////////////////////////////////////////
        InputStream inputStream2 = ImageConverter.imageConverter(file, 730, true);
        byte[] bytes2 = IOUtils.toByteArray(inputStream2);
        // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
        // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
        objectMetadata.setContentLength(bytes2.length);
        ByteArrayInputStream byteArrayIs2 = new ByteArrayInputStream(bytes2);
        // 썸네일 (50KB)
        s3Client.putObject(new PutObjectRequest(bucket, fileName3, byteArrayIs2, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        ////////////////////////////////////////////////////////////////////////
        // 270 사이즈
        ////////////////////////////////////////////////////////////////////////
        InputStream inputStream3 = ImageConverter.imageConverter(file, 270, true);
        byte[] bytes3 = IOUtils.toByteArray(inputStream3);
        // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
        // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
        objectMetadata.setContentLength(bytes3.length);
        ByteArrayInputStream byteArrayIs3 = new ByteArrayInputStream(bytes3);
        // 썸네일 (50KB)                    
        s3Client.putObject(new PutObjectRequest(bucket, fileName2, byteArrayIs3, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        ////////////////////////////////////////////////////////////////////////
        // 120 사이즈
        ////////////////////////////////////////////////////////////////////////
        InputStream inputStream4 = ImageConverter.imageConverter(file, 120, true);
        byte[] bytes4 = IOUtils.toByteArray(inputStream4);
        // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
        // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
        objectMetadata.setContentLength(bytes4.length);
        ByteArrayInputStream byteArrayIs4 = new ByteArrayInputStream(bytes4);
        // 썸네일 (50KB)                    
        s3Client.putObject(new PutObjectRequest(bucket, fileName1, byteArrayIs4, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * 일반 파일 업로드
     * @param file
     * @param baseDir
     * @return
     * @throws IOException
     */
    public String uploadFile(File file, String baseDir) throws IOException {
        String fileName = baseDir + "/" + UUID.randomUUID().toString();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] bytes = Files.readAllBytes(file.toPath());
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        s3Client.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
//        
//        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
//            .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucket, fileName).toString();
    }


    /**
     * 일반 파일 업로드
     * @param file
     * @param baseDir
     * @return
     * @throws IOException
     */
    public String editorUploadFile(byte[] bytes, String baseDir) throws IOException {
        String fileName = baseDir + UUID.randomUUID().toString();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        s3Client.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString();
    }
    
    
    public String downloadFile(URL url, String fileName) {

        try
        {
            String fileName1 = "thumb1_" + fileName; // 120
            String fileName2 = "thumb2_" + fileName; // 270
            String fileName3 = "thumb3_" + fileName; // 730
            // thumb1_ => 120, thumb2_ => 270, thumb3_ => 730    
            
            ObjectMetadata objectMetadata = new ObjectMetadata();

        //    S3Object s3object = s3Client.getObject(new GetObjectRequest(bucket, fileName));

            //S3ObjectInputStream objectInputStream = s3object.getObjectContent();
            //byte[] bytes = IOUtils.toByteArray(objectInputStream);
            // InputStream inputStream11 = s3object.getObjectContent();
            // InputStream inputStream22 = s3object.getObjectContent();
            // InputStream inputStream33 = s3object.getObjectContent();

            // Image image1 = ImageIO.read(s3object.getObjectContent());
            // Image image2 = ImageIO.read(s3object.getObjectContent());
            // Image image3 = ImageIO.read(s3object.getObjectContent());


            Image image = ImageIO.read(url);
            // Image image2 = ImageIO.read(url);
            // Image image3 = ImageIO.read(url);

            ////////////////////////////////////////////////////////////////////////
            // 730 사이즈
            ////////////////////////////////////////////////////////////////////////
            InputStream inputStream2 = ImageConverter.imageConverter2(image, 730, true);
            byte[] bytes2 = IOUtils.toByteArray(inputStream2);

            // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
            // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
            objectMetadata.setContentLength(bytes2.length);
            ByteArrayInputStream byteArrayIs2 = new ByteArrayInputStream(bytes2);

            // 썸네일 (50KB)                    
            s3Client.putObject(new PutObjectRequest(bucket, fileName3, byteArrayIs2, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

            log.info("fileName3 = " + fileName3);
            ////////////////////////////////////////////////////////////////////////
            // 270 사이즈
            ////////////////////////////////////////////////////////////////////////
            InputStream inputStream3 = ImageConverter.imageConverter2(image, 270, true);
            byte[] bytes3 = IOUtils.toByteArray(inputStream3);

            // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
            // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
            objectMetadata.setContentLength(bytes3.length);
            ByteArrayInputStream byteArrayIs3 = new ByteArrayInputStream(bytes3);

            // 썸네일 (50KB)                    
            s3Client.putObject(new PutObjectRequest(bucket, fileName2, byteArrayIs3, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

            log.info("fileName2 = " + fileName2);
            ////////////////////////////////////////////////////////////////////////
            // 120 사이즈
            ////////////////////////////////////////////////////////////////////////
            InputStream inputStream4 = ImageConverter.imageConverter2(image, 120, true);
            byte[] bytes4 = IOUtils.toByteArray(inputStream4);

            // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
            // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
            objectMetadata.setContentLength(bytes4.length);
            ByteArrayInputStream byteArrayIs4 = new ByteArrayInputStream(bytes4);

            // 썸네일 (50KB)                    
            s3Client.putObject(new PutObjectRequest(bucket, fileName1, byteArrayIs4, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

            log.info("fileName1 = " + fileName1);

            return fileName1;
        }
        catch(Exception ex)
        {
            log.error("downloadFile", ex);
        }

        return "";
    }

    public String uploadPricesearchingImage(File file) throws IOException {
        
        String fileName = file.getName();
        String fileName1 = "thumb1_" + fileName; // 120
        String fileName2 = "thumb2_" + fileName; // 270
        String fileName3 = "thumb3_" + fileName; // 730
        // thumb1_ => 120, thumb2_ => 270, thumb3_ => 730    
        
        ObjectMetadata objectMetadata = new ObjectMetadata();

        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));

        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        String pricesearchingBucket = bucket + "/pricesearching";
        
        s3Client.putObject(new PutObjectRequest(pricesearchingBucket, fileName, byteArrayInputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        ////////////////////////////////////////////////////////////////////////
        // 730 사이즈
        ////////////////////////////////////////////////////////////////////////
        InputStream inputStream2 = ImageConverter.imageConverter(file, 730, true);
        byte[] bytes2 = IOUtils.toByteArray(inputStream2);

        // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
        // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
        objectMetadata.setContentLength(bytes2.length);
        ByteArrayInputStream byteArrayIs2 = new ByteArrayInputStream(bytes2);

        // 썸네일 (50KB)                    
        s3Client.putObject(new PutObjectRequest(pricesearchingBucket, fileName3, byteArrayIs2, objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));

        ////////////////////////////////////////////////////////////////////////
        // 270 사이즈
        ////////////////////////////////////////////////////////////////////////
        InputStream inputStream3 = ImageConverter.imageConverter(file, 270, true);
        byte[] bytes3 = IOUtils.toByteArray(inputStream3);

        // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
        // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
        objectMetadata.setContentLength(bytes3.length);
        ByteArrayInputStream byteArrayIs3 = new ByteArrayInputStream(bytes3);

        // 썸네일 (50KB)                    
        s3Client.putObject(new PutObjectRequest(pricesearchingBucket, fileName2, byteArrayIs3, objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));


        ////////////////////////////////////////////////////////////////////////
        // 120 사이즈
        ////////////////////////////////////////////////////////////////////////
        InputStream inputStream4 = ImageConverter.imageConverter(file, 120, true);
        byte[] bytes4 = IOUtils.toByteArray(inputStream4);

        // Content Length 처리를 안할 경우 아래와 같은 warning 메시지가 뜬다.
        // No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
        objectMetadata.setContentLength(bytes4.length);
        ByteArrayInputStream byteArrayIs4 = new ByteArrayInputStream(bytes4);

        // 썸네일 (50KB)                    
        s3Client.putObject(new PutObjectRequest(pricesearchingBucket, fileName1, byteArrayIs4, objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));
            
        return fileName;
    }

    /**
     * 파일 삭제
     * @param a3Key
     */
    public void fileDelete(String a3Key) {
//    	 DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, a3Key);
//    	 s3Client.deleteObject(deleteObjectRequest);
    }
    
    /**
     * 폴더 전체 삭제
     * @param a3Key
     */
	public void folderDelete(String a3Key) {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket).withPrefix(a3Key);
		ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
			fileDelete(objectSummary.getKey());
		}
	}
    
	/**
	 * 파일 다운로드
	 * @param a3Key
	 * @return
	 * @throws IOException
	 */
	public byte[] getObjectRequest(String a3Key) throws IOException {
		S3Object s3object = s3Client.getObject(new GetObjectRequest(bucket, accessKey));
		S3ObjectInputStream objectInputStream = s3object.getObjectContent();
		return IOUtils.toByteArray(objectInputStream);
	}
}