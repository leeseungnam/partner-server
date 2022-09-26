package kr.wrightbrothers.apps.queue.service;

import kr.wrightbrothers.apps.queue.dto.ProductSendDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductQueueService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";

    // SNS 입점몰 상품 정보 조회
    public ProductSendDto findProductSnsData(String partnerCode,
                                             String productCode) {
        return ProductSendDto.builder()
                .partnerCode(partnerCode)
                .product(dao.selectOne(namespace + "findProduct", productCode))
                .basicSpec(dao.selectOne(namespace + "findBasicSpec", productCode))
                .sellInfo(dao.selectOne(namespace + "findSellInfo", productCode))
                .optionList(dao.selectList(namespace + "findOptionList", productCode))
                .delivery(dao.selectOne(namespace + "findDelivery", productCode))
                .infoNotice(dao.selectOne(namespace + "findInfoNotice", productCode))
                .guide(dao.selectOne(namespace + "findGuide", productCode))
                .build();
    }

    // SQS 입점몰 상품 등록
    public void insertProductSqsData() {

    }

    // SQS
    public void updateProductSqsData() {

    }
}
