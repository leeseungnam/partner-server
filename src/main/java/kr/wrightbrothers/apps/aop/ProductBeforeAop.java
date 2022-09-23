package kr.wrightbrothers.apps.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
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
public class ProductBeforeAop {

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

    /**
     * 상품 전시 여부에 대한 변경 유효성 체크
     *
     * @param productNo 상품코드
     * @param changeDisplayFlag 변경 전시 여부
     */
    private void validDisplayChange(String productNo, String changeDisplayFlag) {
        if (changeDisplayFlag.equals(dao.selectOne(namespace + "findProductDisplayFlag", productNo))) {
            log.error("Product Display Change Error.");
            log.error("ProductCode::{}, ChangeDisplayFlag::{}", productNo, changeDisplayFlag);
            throw new WBBusinessException(
                    ErrorCode.INVALID_PRODUCT_DISPLAY.getErrCode(),
                    new String[]{changeDisplayFlag.equals("Y") ? "미노출" : "노출"}
            );
        }

    }

    /**
     * 상품 상태 값 변경 유효성 체크
     *
     * 상품 판매 : 판매종료 / 예약 중 상태에서만 변경 가능
     * 판매 종료, 예약 중 : 판매중인 상태에서만 변경 가능
     *
     * @param productNo 상품코드
     * @param changeStatusCode 변경 상태코드
     */
    private void validStatusChange(String productNo, String changeStatusCode) {
        // 현재 상품 상태 코드
        String currentStatusCode = dao.selectOne(namespace + "findProductStatus", productNo);
        log.debug("Product Current Status::{}", currentStatusCode);
        log.debug("Product Change Status::{}", changeStatusCode);

        switch (ProductStatusCode.of(changeStatusCode)) {
            case SALE:
                if (!(
                        ProductStatusCode.END_OF_SALE.getCode().equals (currentStatusCode) ||
                                ProductStatusCode.RESERVATION.getCode().equals(changeStatusCode)
                ))
                    throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"판매종료/예약중인"});
            case END_OF_SALE:
                if (!ProductStatusCode.SALE.getCode().equals(changeStatusCode))
                    throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"판매중인"});
            case RESERVATION:
                if (!ProductStatusCode.SALE.getCode().equals(changeStatusCode))
                    throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"판매중인"});
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

    @Before(value = "execution(* kr.wrightbrothers.apps.product.service.*Service.update*(..))")
    public void productStatusCheck(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        // 상품 상태 / 노출 일괄 변경 유효성 체크
        if (object.has("productCodeList"))
            Arrays.stream(new ObjectMapper()
                            .convertValue(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow(), StatusUpdateDto.class)
                            .getProductCodeList()
                    )
                    .forEach(productCode -> {
                        // 노출 상태 변경
                        if (object.getString("statusType").equals("DP")) {
                            validDisplayChange(productCode, object.getString("statusValue"));
                            return;
                        }

                        // 판매 상태 변경
                        validStatusChange(productCode, object.getString("statusValue"));
                    });
        // 상품 상태 변경 유효성 체크
        else if (object.has("product"))
            validStatusChange(
                    object.getJSONObject("product").getString("productCode"),
                    object.getJSONObject("sellInfo").getString("productStatusCode")
            );
    }

}
