package kr.wrightbrothers.apps.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.address.dto.AddressAuthDto;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
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
public class ServiceBeforeAop {

    private final WBCommonDao dao;

    /**
     * 스토어 소유의 등록된 주소록인지 유효성 체크
     * 소유에 대한 유효성 포인트는 조회, 수정, 삭제 요청 기능에 대해 체크 함
     */
    @Before(value =
            "execution(* kr.wrightbrothers.apps.address.service.*Service.update*(..)) ||" +
            "execution(* kr.wrightbrothers.apps.address.service.*Service.findAddress(..)) ||" +
            "execution(* kr.wrightbrothers.apps.address.service.*Service.delete*(..))"
    )
    public void ownAddressCheck(JoinPoint joinPoint) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        if (dao.selectOne(
                "kr.wrightbrothers.apps.address.query.Address.isAddressAuth",
                AddressAuthDto.builder()
                        .partnerCode(jsonObject.getString("partnerCode"))
                        .addressNo(jsonObject.getLong("addressNo"))
                        .build())
        )
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
    }


}
