package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class UserPrincipalAop {

    @Before(value =
            "execution(* kr.wrightbrothers..*Controller.*(..)) &&" +
            "@annotation(kr.wrightbrothers.apps.common.annotation.UserPrincipalScope)"
    )
    public void convertUserDate(JoinPoint joinPoint) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Arrays.stream(joinPoint.getArgs()).forEach(object -> {
            Method[] methods = object.getClass().getMethods();

            Arrays.stream(methods).forEach(method -> {
                try {
                    if (method.getName().equals("setAopUserId"))
                        method.invoke(object, userPrincipal.getUsername());
                    if (method.getName().equals("setAopPartnerCode"))
                        method.invoke(object, userPrincipal.getUserAuth().getPartnerCode());
                    if (method.getName().equals("setAopPartnerKind"))
                        method.invoke(object, userPrincipal.getUserAuth().getPartnerKind());
                } catch (Exception e) {
                    log.error("UserPrincipal Convertor Dto", e);
                }
            });
        });
    }

}
