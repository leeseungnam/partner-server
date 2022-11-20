package kr.wrightbrothers.apps.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

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
        if (dao.selectOne(namespace + "isProductAuth", paramDto, PartnerKey.WBDataBase.Alias.Admin)) {
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
        if (changeDisplayFlag.equals(dao.selectOne(namespace + "findProductDisplayFlag", productNo, PartnerKey.WBDataBase.Alias.Admin))) {
            log.error("Product Display Change Error.");
            log.error("ProductCode::{}, ChangeDisplayFlag::{}", productNo, changeDisplayFlag);
            throw new WBBusinessException(
                    ErrorCode.INVALID_PRODUCT_DISPLAY.getErrCode(),
                    new String[]{changeDisplayFlag.equals("Y") ? "미노출" : "노출"}
            );
        }

    }

    /**
     * <pre>
     *     상품 상태 값 변경 유효성 체크
     *
     *     상품 판매 : 판매종료 / 예약 중 상태에서만 변경 가능
     *     판매 종료, 예약 중 : 판매중인 상태에서만 변경 가능
     *</pre>
     *
     * @param productNo 상품코드
     * @param changeStatusCode 변경 상태코드
     * @param changeLog 변경 로그 정보
     */
    private void validStatusChange(String productNo, String changeStatusCode, String changeLog) {
        // 현재 상품 상태 코드
        String currentStatusCode = dao.selectOne(namespace + "findProductStatus", productNo, PartnerKey.WBDataBase.Alias.Admin);
        log.debug("Product Current Status::{}", currentStatusCode);
        log.debug("Product Change Status::{}", changeStatusCode);
        log.debug("Product Change Log::{}", changeLog);

        // 판매완료 수정 불가
        if (ProductStatusCode.END_OF_SALE.getCode().equals(currentStatusCode))
            throw new WBBusinessException(ErrorCode.END_OF_SALE.getErrCode(), new String[]{"판매 완료"});

        // 상태 변경 아닐 시 종료
        if (currentStatusCode.equals(changeStatusCode)) return;

        switch (ProductStatusCode.of(changeStatusCode)) {
            case SALE:
                if (!ProductStatusCode.RESERVATION.getCode().equals(currentStatusCode)
                        && !(ProductStatusCode.PRODUCT_INSPECTION.getCode().equals(currentStatusCode) & changeLog.contains("검수 완료"))
                        && !ProductStatusCode.SOLD_OUT.getCode().equals(currentStatusCode))
                    throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"예약중/판매완료"});
                break;
            case END_OF_SALE:
            case RESERVATION:
                if (!ProductStatusCode.SALE.getCode().equals(currentStatusCode))
                    throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"판매중인"});
                break;
        }
    }

    @Before(value =
            "execution(* kr.wrightbrothers.apps.product.service.*Service.update*(..)) ||" +
            "execution(* kr.wrightbrothers.apps.product.service.*Service.findProduct(..))"
    )
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
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

    /**
     * <pre>
     * 스토어 소유의 상품을 변경할때 상태값의 유효성을 체크 합니다.
     *
     * 상품의 상태값은 두가지로 전시 여부에 대한 상태, 판매 진행에 대한 상태로 나눕니다.
     * 변경 불가에 대한 정책은 아래와 같으니 해당 부분 업무에 있어 참고 바랍니다.
     *
     * 현재 PointCut 영역은 패키지 product -> service 아래 함수 네이밍으로 되어 있습니다.
     * 추가적인 상태값 변경에 대한 유효성 체크 필요 시 아래 구조를 참고하여 작업 하시기 바랍니다.
     *
     * 전시 상태 변경 (검수단계 상태에서는 예외처리)
     *     - 노출 : 미노출 상태에서 노출 변경 가능
     *     - 미노출 : 노출 상태에서 미노출 변경 가능
     * 상품 상태 변경 (검수단계 상태에서는 예외처리)
     *     - 판매 : 판매종료/예약 중 상태에서 가능
     *     - 판매종료 : 판매 상태에서 가능
     *     - 예약중 : 판매 상태에서 가능
     * </pre>
     */
    @Before(value = "execution(* kr.wrightbrothers.apps.product.service.*Service.update*(..))")
    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Global)
    public void productStatusCheck(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        // 상품 상태 / 노출 일괄 변경 유효성 체크
        if (object.has("productCodeList"))
            Arrays.stream(new ObjectMapper()
                            .convertValue(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow(), StatusUpdateDto.class)
                            .getProductCodeList()
                    )
                    .forEach(productCode -> {
                        // 검수단계 예외처리
                        if (dao.selectOne(namespace + "isProductInspection", productCode, PartnerKey.WBDataBase.Alias.Admin))
                            throw new WBBusinessException(ErrorCode.INVALID_PRODUCT_STATUS.getErrCode(), new String[]{"검수단계의 상품은 검수 승인 후 판매중/예약중/판매완료/판매종료"});

                        // 노출 상태 변경
                        if (object.getString("statusType").equals("DP")) {
                            validDisplayChange(productCode, object.getString("statusValue"));
                            return;
                        }

                        // 판매 상태 변경
                        validStatusChange(productCode, object.getString("statusValue"), "");
                    });
        // 상품 상태 변경 유효성 체크
        else if (object.has("product")) {
            validStatusChange(
                    object.getJSONObject("product").getString("productCode"),
                    object.getJSONObject("sellInfo").getString("productStatusCode"),
                    // 검수 요청에서 승인나지 않은 상태에서 상품 판매 변경 시 예외 처리를 위한 데이터
                    object.has("changeLogList") ?
                            object.get("changeLogList") instanceof JSONArray ?
                                    object.getJSONArray("changeLogList").toString() : object.getString("changeLogList")
                                    : ""
            );
        }

    }

}
