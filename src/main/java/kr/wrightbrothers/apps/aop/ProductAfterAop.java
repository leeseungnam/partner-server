package kr.wrightbrothers.apps.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.product.dto.ProductAuthDto;
import kr.wrightbrothers.apps.product.dto.StatusUpdateDto;
import kr.wrightbrothers.apps.queue.ProductQueue;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import kr.wrightbrothers.framework.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class ProductAfterAop {

    private final WBCommonDao dao;
    private final ProductQueue productQueue;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";

    @AfterReturning(value = "execution(* kr.wrightbrothers.apps.product.service.*Service.update*(..))")
    public void sendProductSnsData(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        // 입점몰 API -> ADMIN 2.0 API
        // AWS SNS Message Queue 상품 변경에 따른 발송 처리
        if (object.has("productCodeList"))
            // 상품 상태 일괄 변경에 따른 SNS 발송 처리
            Arrays.stream(new ObjectMapper()
                            .convertValue(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow(), StatusUpdateDto.class)
                            .getProductCodeList()
                    )
                    .forEach(productCode -> {
                        productQueue.sendToAdmin(
                                object.getString("partnerCode"),
                                productCode,
                                PartnerKey.TransactionType.Update
                        );
                        log.debug("Product Status Batch Update Send SNS. ProductCode::{}", productCode);
                    });
        else if (object.has("product")) {
            // 상품 변경에 따른 SNS 발송 처리
            productQueue.sendToAdmin(
                    object.getJSONObject("product").getString("partnerCode"),
                    object.getJSONObject("product").getString("productCode"),
                    PartnerKey.TransactionType.Update
            );
            log.debug("Product Update Send SNS. ProductCode::{}", object.getString("productCode"));
        }
    }

}
