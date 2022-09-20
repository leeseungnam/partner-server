package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.product.dto.ProductAuthDto;
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

    @Before(value =
            "execution(* kr.wrightbrothers.apps.product.service.*Service.update*(..)) ||" +
            "execution(* kr.wrightbrothers.apps.product.service.*Service.findProduct(..))"
    )
    public void ownProductCheck(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));
        ProductAuthDto paramDto;

        // product 존재 여부 체크
        if (object.has("product"))
            paramDto = ProductAuthDto.builder()
                    .partnerCode(object.getJSONObject("product").getString("partnerCode"))
                    .productCode(object.getJSONObject("product").getString("productCode"))
                    .build();
        else
            paramDto = ProductAuthDto.builder()
                    .partnerCode(object.getString("partnerCode"))
                    .productCode(object.getString("productCode"))
                    .build();

        if (dao.selectOne(namespace + "isProductAuth", paramDto)) {
            log.error("Product Auth Error.");
            log.error("PartnerCode::{}, ProductCode::{}", object.getString("partnerCode"), object.getString("productCode"));
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        }
    }

}
