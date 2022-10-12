package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.order.dto.OrderAuthDto;
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
public class OrderBeforeAop {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.order.query.Order.";

    /**
     * <pre>
     *     스토어 소유의 등록된 주문 정보인지 유효성 체크
     *     소유에 대한 유효성 포인트는 조회, 수정 요청 기능에 대해 체크 합니다.
     *
     *     현재 PointCut 영역은 패키지 order -> service 아래 함수 네이밍으로 되어 있습니다.
     *     추가적인 소유에 대한 권한 체크 부분에서는 함수명, 파라미터의 구조를 참고하여 작업 하시기 바랍니다.
     *
     *     Object 필수 데이터 : partnerCode, orderNo
     * </pre>
     */
    @Before(value =
            "execution(* kr.wrightbrothers.apps.order.service.*Service.update*(..)) || " +
            "(execution(* kr.wrightbrothers.apps.order.service.*Service.find*(..)) && " +
            "!execution(* kr.wrightbrothers.apps.order.service.*Service.*List(..)) && " +
            "!execution(* kr.wrightbrothers.apps.order.service.*Service.*ToOrder(..)) && " +
            "!execution(* kr.wrightbrothers.apps.order.service.*Service.*Statistics(..)))"
    )
    public void ownOrderCheck(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        if (dao.selectOne(namespace + "isOrderAuth",
                OrderAuthDto.builder()
                        .partnerCode(object.getString("partnerCode"))
                        .orderNo(object.getString("orderNo"))
                        .build())) {

            log.error("Order Auth Error.");
            log.error("PartnerCode::{}, OrderNo::{}", object.getString("partnerCode"), object.getString("orderNo"));
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        }
    }

}
