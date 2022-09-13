package kr.wrightbrothers.framework.lang;

import io.jsonwebtoken.ExpiredJwtException;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.framework.support.WBCommon;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.util.RandomKey;
import kr.wrightbrothers.framework.util.StaticContextAccessor;
import kr.wrightbrothers.framework.util.WBMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class WBGlobalException {

    /**
     * 라이트브라더스 CustomException Handler
     */
    @ExceptionHandler({
            WBBusinessException.class
    })
    private ResponseEntity<JSONObject> customerException(WBBusinessException ex) {
        if (ObjectUtils.isEmpty(ex)) ex.getCause();

        log.error("= WBBusiness ERROR. ===============================");
        log.error("UUID, {}", RandomUtil.getUUID());
        log.error("ERROR CODE, {}", ex.getErrorCode());
        log.error("ERROR TYPE, {}", ex.getType());
        log.error("ERROR MESSAGE, {}", StaticContextAccessor.getBean(WBMessage.class)
                .getMessage(
                        ex.getErrorCode(),
                        ex.getType(),
                        ex.getMsgConvert()
                )
        );
        log.error("===================================================");
        
        return new ResponseEntity<>(
                exceptionResponse(ex.getErrorCode(), ex.getType(), ex.getMsgConvert()),
                HttpStatus.OK
        );
    }

    /**
     * ExpiredJwtException Handler
     */
    @ExceptionHandler({
    	ExpiredJwtException.class
    })
    private ResponseEntity<JSONObject> jwtException(Exception e) {
    	 return new ResponseEntity<>(exceptionResponse(3, WBKey.Message.Type.Notification), HttpStatus.OK);
    }
    
    /**
     * Exception Handler
     */
    @ExceptionHandler({
            Exception.class
    })
    private ResponseEntity<JSONObject> exception(Exception ex) {
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));

        log.error("= INTERNAL SERVER ERROR. ==========================");
        log.error("UUID, {}", RandomUtil.getUUID());
        log.error("EXCEPTION, {}", writer);
        log.error("===================================================");

        // 세션 종료에 따른 매시지 처리
        if (ex instanceof NullPointerException) {
            HttpServletRequest request =
                    ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            if (request.getAttribute(WBKey.JWTExpired) != null && (boolean) request.getAttribute(WBKey.JWTExpired)) {
                request.removeAttribute(WBKey.JWTExpired);
                return new ResponseEntity<>(
                        exceptionResponse(3, WBKey.Message.Type.Notification),
                        HttpStatus.OK
                );
            }
        }

        return new ResponseEntity<>(
                exceptionResponse(0, WBKey.Message.Type.Error),
                HttpStatus.OK
        );
    }

    /**
     * 예외 Api Response
     */
    private JSONObject exceptionResponse(int errorCode, String errorType) {
        return exceptionResponse(errorCode, errorType, null);
    }

    /**
     * 예외 Api Response
     */
    public static JSONObject exceptionResponse(int errorCode, String errorType, String[] convert) {
        JSONObject json = new JSONObject();
        json.put("WBCommon",
                WBCommon.builder()
                        .state(WBKey.Error)
                        .uuid(RandomKey.getUUID())
                        .token(
                                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                                        .getRequest().getHeader(WBKey.Jwt.HeaderName)
                        )
                        .msgCode(StringUtils.leftPad(String.valueOf(errorCode), 4, "0"))
                        .msgType(errorType)
                        .message(
                                StaticContextAccessor.getBean(WBMessage.class)
                                        .getMessage(
                                                errorCode,
                                                errorType,
                                                convert
                                        )
                        )
                        .build()
        );

        return json;
    }

}
