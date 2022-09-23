package kr.wrightbrothers.apps.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.product.dto.StatusUpdateDto;
import kr.wrightbrothers.apps.queue.ProductQueue;
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

    private final ProductQueue productQueue;

    /**
     * <pre>
     *     스토어 상품 등록, 수정의 작업 로직이 구현되어 있는 해당 상품 서비스 함수가 정상적으로 실행된 후
     *     아래 구현된 로직이 실행 됩니다.
     *
     *     해당 로직은 현재 등록 되어있는 스토어 상품 정보를 조회하여 Admin 2.0 API 서버에 등록 또는 변동 된
     *     상품의 정보를 명세서에 맞게 조합 후 Message Queue 전송 처리를 합니다.
     *
     *     해당 전송 결과 로그는 Admin 2.0 모니터링 테이블에 결과가 수신되고 있으니 해당 테이블을 통하여
     *     결과를 참고하면 됩니다.
     *
     *     현재 PointCut 영억은 패키지 product -> service -> update*, insert*(ProductService)
     *     함수 네이밍으로 되어있습니다. 추가 필요부분이 있을 경우 RequestBody 구조를 아래와 같이 하시기 바랍니다.
     *
     *     신규 생성 : product Object 필요 (참고 ProductInsertDto.java)
     *     수정 생성 : product Object, changeLogList Array 필요 (참고 ProductUpdateDto.java)
     * </pre>
     */
    @AfterReturning(value =
                    "execution(* kr.wrightbrothers.apps.product.service.*Service.update*(..)) ||" +
                    "execution(* kr.wrightbrothers.apps.product.service.ProductService.insert*(..))"
    )
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
                        log.debug("Product Batch Update Send SNS. ProductCode::{}", productCode);
                    });
        else if (object.has("product")) {
            // 상품 등록 / 변경에 따른 SNS 발송 처리
            productQueue.sendToAdmin(
                    object.getJSONObject("product").getString("partnerCode"),
                    object.getJSONObject("product").getString("productCode"),
                    // 변경 사항 로그 유무를 통한 등록 / 수정 구분
                    object.has("changeLogList") ?
                            PartnerKey.TransactionType.Update : PartnerKey.TransactionType.Insert
            );
            log.debug("Product Send SNS. ProductCode::{}", object.getJSONObject("product").getString("productCode"));
        }
    }

}
