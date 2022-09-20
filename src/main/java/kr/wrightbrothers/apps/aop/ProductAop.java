package kr.wrightbrothers.apps.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.product.dto.ProductAuthDto;
import kr.wrightbrothers.apps.product.dto.StatusUpdateDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import kr.wrightbrothers.framework.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class ProductAop {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.product.query.Product.";

    /**
     * 스토어 소유의 등록된 상품인지 유효성 체크
     */
    private void ownCheck(ProductAuthDto paramDto) {
        if (dao.selectOne(namespace + "isProductAuth", paramDto)) {
            log.error("Product Auth Error.");
            log.error("PartnerCode::{}, ProductCode::{}", paramDto.getPartnerCode(), paramDto.getProductCode());
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        }
    }

    @Before(value =
            "execution(* kr.wrightbrothers.apps.product.service.*Service.update*(..)) ||" +
            "execution(* kr.wrightbrothers.apps.product.service.*Service.findProduct(..))"
    )
    public void ownProductCheck(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        // 상품 상태 일괄 변경 소유권 조회
        if (object.has("productCodeList"))
            // 상품코드 배열 foreach 변환 후 소유권 권한체크
            Arrays.stream(new ObjectMapper()
                            .convertValue(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow(), StatusUpdateDto.class)
                            .getProductCodeList()
                    )
                    .forEach(productCode -> ownCheck(ProductAuthDto.builder()
                            .partnerCode(object.getString("partnerCode"))
                            .productCode(productCode)
                            .build()));
        // 상품 수정 소유권 조회
        else if (object.has("product"))
            ownCheck(ProductAuthDto.builder()
                    .partnerCode(object.getJSONObject("product").getString("partnerCode"))
                    .productCode(object.getJSONObject("product").getString("productCode"))
                    .build());
        // 그외
        else
            ownCheck(ProductAuthDto.builder()
                    .partnerCode(object.getString("partnerCode"))
                    .productCode(object.getString("productCode"))
                    .build());
    }

}
