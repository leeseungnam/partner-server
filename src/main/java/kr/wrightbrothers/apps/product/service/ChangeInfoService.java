package kr.wrightbrothers.apps.product.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.product.dto.ChangeInfoDto;
import kr.wrightbrothers.apps.product.dto.ChangeInfoListDto;
import kr.wrightbrothers.apps.product.dto.ProductDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ChangeInfoService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.product.query.ChangeInfo.";

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void insertChangeInfo(ChangeInfoDto.ReqBody paramDto) {
        if (ObjectUtils.isEmpty(paramDto.getProductLog())) return;
        // 상태 변경 이력 등록
        dao.insert(namespace + "insertChangeInfo", paramDto);
    }

    public ChangeInfoListDto.Response findProductChangeHistory(ChangeInfoListDto.Param paramDto) {
        ProductDto.ResBody product = dao.selectOne("kr.wrightbrothers.apps.product.query.Product.findProduct", paramDto.getProductCode(), PartnerKey.WBDataBase.Alias.Admin);

        return ChangeInfoListDto.Response.builder()
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                // 상품 상태 변경 이력 조회
                .changeHistory(dao.selectList(namespace + "findProductChangeHistory", paramDto))
                .build();
    }

}
