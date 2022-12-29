package kr.wrightbrothers.apps.temporary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.constants.StorageConst;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.product.dto.ProductInsertDto;
import kr.wrightbrothers.apps.temporary.dto.TemporaryDto;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TemporaryService {

    private final WBCommonDao dao;
    private final FileService fileService;
    private final String namespace = "kr.wrightbrothers.apps.temporary.query.Temporary.";

    public TemporaryDto.Response findTemporary(TemporaryDto.Param paramDto) {
        return dao.selectOne(namespace + "findTemporary", paramDto);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void mergeTemporary(TemporaryDto.ReqBody paramDto) throws JsonProcessingException {
        dao.insert(namespace + "mergeTemporary", paramDto);

        // 상품 임시저장 데이터 경우 사진 등록 처리
        if (StorageConst.Type.PRODUCT.getType().equals(paramDto.getStorageType())) {
            ProductInsertDto product = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                    .readValue(paramDto.getStorageData(), ProductInsertDto.class);
            product.setAopUserId(paramDto.getUserId());
            // AWS 처리
            fileService.s3FileUpload(product.getFileList(), WBKey.Aws.A3.Product_Img_Path + "0000000000", true);
        }

    }

    public void deleteTemporary(TemporaryDto.Param paramDto) {
        dao.delete(namespace + "deleteTemporary", paramDto);
    }
}
