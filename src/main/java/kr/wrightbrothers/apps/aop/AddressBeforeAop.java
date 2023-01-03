package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.address.dto.AddressAuthDto;
import kr.wrightbrothers.apps.common.util.ErrorCode;
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
public class AddressBeforeAop {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.address.query.Address.";

    /**
     * <pre>
     *     스토어 소유의 등록된 주소록인지 유효성 체크
     *     소유에 대한 유효성 포인트는 조회, 수정, 삭제 요청 기능에 대해 체크 합니다.
     *
     *     현재 PointCut 영역은 패키지 address -> service 아래 함수 네이밍으로 되어 있습니다.
     *     추가적인 주소록의 파트너 소유권 시 아래 구조를 참고하여 작업 하시기 바랍니다.
     *
     *     Object 필수 데이터 : partnerCode, addressNo
     * </pre>
     */
    @Before(value =
            "execution(* kr.wrightbrothers.apps.address.service.*Service.update*(..)) ||" +
            "execution(* kr.wrightbrothers.apps.address.service.*Service.findAddress(..)) ||" +
            "execution(* kr.wrightbrothers.apps.address.service.*Service.delete*(..))"
    )
    public void ownAddressCheck(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        if ((boolean) dao.selectOne(namespace + "isAddressAuth",
                AddressAuthDto.builder()
                        .partnerCode(object.getString("partnerCode"))
                        .addressNo(object.getLong("addressNo"))
                        .build())) {

            log.error("Address Auth Error.");
            log.error("PartnerCode::{}, AddressNo::{}", object.getString("partnerCode"), object.getLong("addressNo"));
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        }

    }


}
