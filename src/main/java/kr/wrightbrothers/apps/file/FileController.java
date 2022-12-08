package kr.wrightbrothers.apps.file;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.dto.FileParamDto;
import kr.wrightbrothers.apps.file.dto.FileDto;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class FileController extends WBController {

    private final String MSG_PRE_FIX = "api.message.common.";
    private final FileService fileService;
    private final MessageSourceAccessor messageSourceAccessor;

    @GetMapping("/files/generate-key")
    public WBModel findFileKey() {
        WBModel response = new WBModel();
        response.addObject(WBKey.File.UploadFileNo, RandomUtil.generateNo());
        return response;
    }

    @PostMapping("/files/upload/{fileNo}")
    public WBModel uploadFile(@RequestParam MultipartFile[] files,
                              @PathVariable String fileNo) {
        WBModel response = new WBModel();
        if (files.length == 0) return response;

        if ("0".equals(fileNo)) fileNo = RandomUtil.generateNo();

        response.addObject(WBKey.File.UploadFileDataSet, fileService.uploadFile(files, fileNo));
        response.addObject(WBKey.File.UploadFileNo, fileNo);
        response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(MSG_PRE_FIX + "save.success"));

        return response;
    }

    @PostMapping("/files/upload-tif/{fileNo}/{productCode}")
    public WBModel uploadTifFile(@RequestParam MultipartFile file,
                                 @PathVariable String fileNo,
                                 @PathVariable String productCode) {
        WBModel response = new WBModel();

        if ("0".equals(fileNo)) fileNo = RandomUtil.generateNo();

        response.addObject(WBKey.File.UploadFileDataSet, fileService.uploadTifFile(file, fileNo, productCode));
        response.addObject(WBKey.File.UploadFileNo, fileNo);
        response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(MSG_PRE_FIX + "save.success"));

        return response;
    }

    @PostMapping("/files/upload-image")
    public WBModel uploadImageFile(@RequestParam MultipartFile file) throws IOException {
        return defaultInsertResponse(fileService.uploadImageFile(file), messageSourceAccessor);
    }

    @GetMapping("/files/{fileNo}")
    public WBModel findFileList(@PathVariable String fileNo) {
        return defaultResponse(fileService.findFileList(fileNo));
    }

    @GetMapping("/files/download")
    public void downloadFile(@RequestParam String fileNo,
                             @RequestParam Long fileSeq,
                             HttpServletResponse response) throws IOException {
        // 다운로드 대상 파일 조회
        FileDto file = fileService.findFile(
                FileParamDto.builder()
                        .fileNo(fileNo)
                        .fileSeq(fileSeq)
                        .build()
        );

        URL url = new URL(file.getFileSource());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +
                URLEncoder.encode(file.getFileOriginalName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20") + ";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentLength(httpURLConnection.getContentLength());
        response.getOutputStream().write(inputStream.readAllBytes());
        response.getOutputStream().flush();
        response.getOutputStream().close();

        httpURLConnection.disconnect();
        inputStream.close();
    }

}
