package kr.wrightbrothers.apps.file;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(tags = {"파일"})
@RestController
@RequiredArgsConstructor
public class FileController extends WBController {

    private final FileService fileService;

    @PostMapping("/files/upload/{fileNo}")
    public WBModel uploadFile(@RequestParam MultipartFile[] files,
                              @PathVariable(required = false) String fileNo) {
        WBModel response = new WBModel();
        if (files.length == 0) return response;

        if ("0".equals(fileNo)) fileNo = RandomUtil.generateNo();

        response.addObject(WBKey.File.UploadFileDataSet, fileService.uploadFile(files, fileNo));
        response.addObject(WBKey.File.UploadFileNo, fileNo);

        return response;
    }

    @PostMapping("/files/upload-tif/{fileNo}/{productCode}")
    public WBModel uploadTifFile(@RequestParam MultipartFile files,
                                 @PathVariable String fileNo,
                                 @PathVariable String productCode) {
        WBModel response = new WBModel();

        if ("0".equals(fileNo)) fileNo = RandomUtil.generateNo();

        response.addObject(WBKey.File.UploadFileDataSet, fileService.uploadTifFile(files, fileNo, productCode));
        response.addObject(WBKey.File.UploadFileNo, fileNo);

        return response;
    }

    @PostMapping("/files/upload-image")
    public WBModel uploadImageFile(@RequestParam MultipartFile file) throws IOException {
        return defaultResponse(fileService.uploadImageFile(file));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.HeaderName, value = "access token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 목록 조회", notes = "파일 대표 번호로 등록되어 있는 파일 목록을 조회")
    @GetMapping("/files/{fileNo}")
    public WBModel findFileList(@ApiParam(value = "파일 대표 번호") @PathVariable String fileNo) {
        return defaultResponse(fileService.findFileList(fileNo));
    }

}
