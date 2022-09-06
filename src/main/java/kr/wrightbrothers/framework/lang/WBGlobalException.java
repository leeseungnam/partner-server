package kr.wrightbrothers.framework.lang;

import io.jsonwebtoken.ExpiredJwtException;
import kr.wrightbrothers.apps.common.util.ErrorCode;
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

        if (ex.getErrorCode() == ErrorCode.INVALID_PARAM.getErrCode()) {
            return new ResponseEntity<>(
                    exceptionValidResponse(
                            ex.getMsgConvert()[0],  // 필드 값
                            ex.getMsgConvert()[1]   // 에러 메시지
                    ),
                    HttpStatus.OK
            );
        }

        log.error("= WBBusiness Error. ===============================");
        log.error("uuid, {}", RandomKey.getUUID());
        log.error("errorCode, {}", ex.getErrorCode());
        log.error("errorType, {}", ex.getType());
        log.error("errorMsg, {}", StaticContextAccessor.getBean(WBMessage.class)
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

        if (!(ex instanceof WBValidateException | ex instanceof MethodArgumentNotValidException)) {
            log.error("= Internal Server Error. ==========================");
            log.error("UUID, {}", RandomKey.getUUID());
            log.error("EXCEPTION, {}", writer);
            log.error("===================================================");
        }
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

        // 커스텀 유효성 검사 에러
        if (ex instanceof WBValidateException) {
            Errors errors = ((WBValidateException) ex).getErrors();
            return new ResponseEntity<>(
                    exceptionValidResponse(
                            errors.getFieldErrors().get(0).getField(),      // 필드 값
                            Objects.requireNonNull(errors.getFieldErrors().get(0).getCodes())[Objects.requireNonNull(errors.getFieldErrors().get(0).getCodes()).length-1]    // 에러 메시지
                    ),
                    HttpStatus.OK
            );
        }

        // 파라미터 유효성 검사 에러
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            return new ResponseEntity<>(
                    exceptionValidResponse(
                            bindingResult.getAllErrors().get(0).getObjectName(),
                            bindingResult.getAllErrors().get(0).getDefaultMessage()
                    ),
                    HttpStatus.OK);
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
    private JSONObject exceptionResponse(int errorCode, String errorType, String[] convert) {
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

    /**
     * 유효성 검사 API Response
     */
	private JSONObject exceptionValidResponse(String field, String message) {

        log.error("= Validation Error. ===============================");
        log.error("UUID, {}", RandomKey.getUUID());
        log.error("PARAMETER, {}", field);
        log.error("MESSAGE, {}", message);
        log.error("===================================================");

        JSONObject json = new JSONObject();
        json.put("WBCommon",
                WBCommon.builder()
                        .state(WBKey.Error)
                        .uuid(RandomKey.getUUID())
                        .token(
                                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                                        .getRequest().getHeader(WBKey.Jwt.HeaderName)
                        )
                        .msgCode(String.valueOf(ErrorCode.INVALID_PARAM.getErrCode()))
                        .msgType(WBKey.Message.Type.Error)
                        //.message(String.format("[%s]%s", field, message))
                        .message(message)
                        .build()
        );

        return json;
    }

}
