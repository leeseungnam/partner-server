package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.template.dto.TemplateAuthDto;
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
public class TemplateBeforeAop {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.template.query.Template.";

    /**
     * <pre>
     *     스토어 소유의 등록된 템플릿인지 유효성 체크
     *     소유에 대한 유효성 포인트는 조회, 수정 요청 기능에 대해 체크 합니다.
     *
     *     현재 PointCut 영역은 패키지 template -> service 아래 함수 네이밍으로 되어 있습니다.
     *     추가적인 템플릿 파트너 소유권 시 아래 구조를 참고하여 작업 하시기 바랍니다.
     *
     *     Object 필수 데이터 : partnerCode, templateNo
     * </pre>
     */
    @Before(value =
            "execution(* kr.wrightbrothers.apps.template.service.*Service.update*(..)) ||" +
            "execution(* kr.wrightbrothers.apps.template.service.*Service.findTemplate(..))"
    )
    public void ownTemplateCheck(JoinPoint joinPoint) throws Exception {
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        if ((boolean) dao.selectOne(namespace + "isTemplateAuth", TemplateAuthDto.builder()
                .partnerCode(object.getString("partnerCode"))
                .templateNo(object.getLong("templateNo"))
                .build())) {

            log.error("Template Auth Error.");
            log.error("PartnerCode::{}, AddressNo::{}", object.getString("partnerCode"), object.getLong("addressNo"));
            throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());
        }
    }

}
