package kr.wrightbrothers.apps.file.service;

import kr.wrightbrothers.apps.common.util.ImageConverter;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.file.dto.FileParamDto;
import kr.wrightbrothers.apps.file.dto.FileDto;
import kr.wrightbrothers.apps.file.dto.FileListDto;
import kr.wrightbrothers.apps.file.dto.FileUploadDto;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileService {

    @Value("${System.File.Temp.Path}")
    private String tempPath;
    private final S3Service s3Service;
    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.file.query.File.";

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
    public List<FileUploadDto> uploadFile(final MultipartFile[] files,
                                          final String fileNo) {
        return Arrays.stream(files).map(file -> {
            try {
                // 폴더 생성
                new File(tempPath).mkdirs();
                // 임시파일 생성
                String randomKey = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
                File tempFile = new File(tempPath + fileNo + randomKey);
                file.transferTo(tempFile);

                // 파일정보 저장
                FileUploadDto fileDto = FileUploadDto.builder()
                        .fileNo(fileNo)
                        .fileOriginalName(file.getOriginalFilename())
                        .fileSource(tempFile.getAbsolutePath())
                        .fileSize(String.valueOf(file.getSize()))
                        .fileStatus("T")
                        .build();
                dao.insert(namespace + "insertFile", fileDto, PartnerKey.WBDataBase.Alias.Admin);

                fileDto.setFileStatus(WBKey.TransactionType.Insert);
                return fileDto;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Admin)
    public FileUploadDto uploadTifFile(final MultipartFile file,
                                       final String fileNo,
                                       final String productCode) {
        final String filePath =
                tempPath + WBKey.Aws.A3.Partner_Img_Path + WBKey.Aws.A3.tif_Img_Path
                        + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + productCode + "/";

        try {
            // 폴더 생성
            new File(filePath).mkdirs();

            File tifFile = ImageConverter.convertTiff(file, filePath, file.getOriginalFilename());
            String fileSource = s3Service.editorUploadFile(Files.readAllBytes(tifFile.toPath()), PartnerKey.Aws.A3.tif_Img_Path);

            // 파일정보 저장
            FileUploadDto fileDto = FileUploadDto.builder()
                    .fileNo(fileNo)
                    .fileOriginalName(file.getOriginalFilename())
                    .fileSource(fileSource)
                    .fileSize(String.valueOf(file.getSize()))
                    .tifPath(tifFile.getPath())
                    .fileStatus("T")
                    .build();
            dao.insert(namespace + "insertFile", fileDto, PartnerKey.WBDataBase.Alias.Admin);

            fileDto.setFileStatus(WBKey.TransactionType.Insert);
            return fileDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String uploadImageFile(final MultipartFile file) throws IOException {
        return s3Service.editorUploadFile(file.getBytes(), PartnerKey.Aws.A3.Partner_Img_Path + PartnerKey.Aws.A3.Editor_Img_Path);
    }

    public List<FileListDto> findFileList(final String fileNo) {
        return dao.selectList(namespace + "findFileList", fileNo, PartnerKey.WBDataBase.Alias.Admin);
    }

    public FileDto findFile(FileParamDto paramDto) {
        return dao.selectOne(namespace + "findFile", paramDto, PartnerKey.WBDataBase.Alias.Admin);
    }
}
