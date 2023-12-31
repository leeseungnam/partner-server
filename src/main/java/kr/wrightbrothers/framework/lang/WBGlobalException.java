package kr.wrightbrothers.framework.lang;

import io.jsonwebtoken.ExpiredJwtException;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.framework.support.WBCommon;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.util.StaticContextAccessor;
import kr.wrightbrothers.framework.util.WBMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.MDC;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class WBGlobalException {

    private String MESSAGE_PREFIX="api.message.";
    private final MessageSourceAccessor messageSourceAccessor;
    public WBGlobalException(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * 라이트브라더스 CustomException Handler
     */
    @ExceptionHandler({
            WBBusinessException.class
    })
    private ResponseEntity<JSONObject> customerException(WBBusinessException ex) {
        if (ObjectUtils.isEmpty(ex)) ex.getCause();

        log.error("= WBBusiness ERROR. ===============================");
        log.error("UUID, {}", MDC.get("thread-id"));
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
     * WBCustomException Handler
     * Message Properties 사용
     */
    @ExceptionHandler({
            WBCustomException.class
    })
    private ResponseEntity<JSONObject> customException(WBCustomException ex) {
        if (ObjectUtils.isEmpty(ex)) ex.getCause();

        log.error("[WBCustomException]:: ===============================");
        log.error("[WBCustomException]:: CODE, {}", ex.getErrorCode());
        log.error("[WBCustomException]:: TYPE, {}", WBKey.Message.Type.Error);
        log.error("[WBCustomException]:: MESSAGE ID, {}", ex.getMessageId());
        log.error("[WBCustomException]:: ===============================");

        return new ResponseEntity<>(
                exceptionResponse(ex.getErrorCode(),ex.getMessageId(), ex.getMessageArgs()),
                HttpStatus.OK
        );
    }

    /**
     * BadCredentialsException Handler
     * security AuthenticationException 처리
     */
    @ExceptionHandler({
            BadCredentialsException.class
    })
    private ResponseEntity<JSONObject> badCredentialsException(BadCredentialsException ex) {
        log.error("[BadCredentialsException]::{}",ex.getMessage());
        return new ResponseEntity<>(
                exceptionResponse(ErrorCode.UNAUTHORIZED_LOGIN, MESSAGE_PREFIX+"user.login.empty", null),
                HttpStatus.OK
        );
    }

    /**
     * InternalAuthenticationServiceException Handler
     * security AuthenticationException(UsernameNotFoundException, LockedException) 처리
     */
    @ExceptionHandler({
            AuthenticationException.class
    })
    private ResponseEntity<JSONObject> AuthenticationException(AuthenticationException ex) {
        if (ObjectUtils.isEmpty(ex)) ex.getCause();

        log.error("[AuthenticationException]::{}", ex.getMessage());
        return new ResponseEntity<>(
                exceptionResponse(ErrorCode.FORBIDDEN, ex.getMessage()),
                HttpStatus.OK
        );
    }

    /**
     * Exception Handler
     */
    @ExceptionHandler({
            Exception.class
    })
    private ResponseEntity<JSONObject> exception(Exception ex) {
        log.error("= INTERNAL SERVER ERROR. ==========================");
        log.error("UUID, {}", MDC.get("thread-id"));
        log.error("EXCEPTION, {}", ExceptionUtils.getStackTrace(ex));
        log.error("===================================================");

        int errorCode = ErrorCode.INTERNAL_SERVER.getErrCode();
        String[] convert = null;

        // 접근 거부 메시지 처리
        if (ex instanceof AccessDeniedException)
            errorCode = ErrorCode.FORBIDDEN.getErrCode();

        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            errorCode = ErrorCode.INVALID_PARAM.getErrCode();
            convert = new String[]{bindingResult.getAllErrors().get(0).getDefaultMessage()};
            // 금액 컴럼 Contains 적용 문자
            String[] moneyParam = {"Amount", "Charge", "chargeBase"};

            if ("Size".equals(bindingResult.getAllErrors().get(0).getCode())) {
                errorCode = ErrorCode.INVALID_TEXT_SIZE.getErrCode();
                convert = new String[]{
                        bindingResult.getAllErrors().get(0).getDefaultMessage(),
                        String.valueOf(Objects.requireNonNull(bindingResult.getAllErrors().get(0).getArguments())[2]),
                        String.valueOf(Objects.requireNonNull(bindingResult.getAllErrors().get(0).getArguments())[1])
                };
            }

            if ("Min".equals(bindingResult.getAllErrors().get(0).getCode())) {
                errorCode = StringUtils.containsAny(Objects.requireNonNull(bindingResult.getFieldError()).getField(), moneyParam) ?
                        ErrorCode.INVALID_MONEY_MIN.getErrCode() : ErrorCode.INVALID_NUMBER_MIN.getErrCode();
                convert = new String[]{
                        bindingResult.getAllErrors().get(0).getDefaultMessage(),
                        String.valueOf(Objects.requireNonNull(bindingResult.getAllErrors().get(0).getArguments())[1])
                };
            }

            if ("Max".equals(bindingResult.getAllErrors().get(0).getCode())) {
                errorCode = StringUtils.containsAny(Objects.requireNonNull(bindingResult.getFieldError()).getField(), moneyParam) ?
                        ErrorCode.INVALID_MONEY_MAX.getErrCode() : ErrorCode.INVALID_NUMBER_MAX.getErrCode();
                convert = new String[]{
                        bindingResult.getAllErrors().get(0).getDefaultMessage(),
                        String.valueOf(Objects.requireNonNull(bindingResult.getAllErrors().get(0).getArguments())[1])
                };
            }

            // 정규표현식에 대한 유효성 에러는 해당 에러 message 처리 한다.
            if ("Pattern".equals(bindingResult.getAllErrors().get(0).getCode())) {
                errorCode = 9999;
                convert = new String[]{bindingResult.getAllErrors().get(0).getDefaultMessage()};
            }

            if ("AssertTrue".equals(bindingResult.getAllErrors().get(0).getCode())) {
                errorCode = ErrorCode.INVALID_BOOLEAN.getErrCode();;
            }
        }

        return new ResponseEntity<>(
                exceptionResponse(errorCode, WBKey.Message.Type.Error, convert),
                HttpStatus.OK
        );
    }

    /**
     * 예외 Api Response
     * Use Direct Message
     */
    private JSONObject exceptionResponse(ErrorCode errorCode, String message) {
        JSONObject json = new JSONObject();
        json.put("WBCommon",
                WBCommon.builder()
                        .state(WBKey.Error)
                        .msgCode(StringUtils.leftPad(String.valueOf(errorCode.getErrCode()), 4, "0"))
                        .msgType(WBKey.Message.Type.Error)
                        .message(message)
                        .build()
        );
        return json;
    }

    /**
     * 예외 Api Response
     * Use Message Properties
     */
    private JSONObject exceptionResponse(ErrorCode errorCode, String messageId, Object[] messageArgs) {
        return exceptionResponse(errorCode, messageId, messageArgs, messageSourceAccessor);
    }
    public static JSONObject exceptionResponse(ErrorCode errorCode, String messageId, Object[] messageArgs, MessageSourceAccessor messageSourceAccessor) {
        JSONObject json = new JSONObject();
        json.put("WBCommon",
                WBCommon.builder()
                        .state(WBKey.Error)
                        .msgCode(StringUtils.leftPad(String.valueOf(errorCode.getErrCode()), 4, "0"))
                        .msgType(WBKey.Message.Type.Error)
                        .message(messageSourceAccessor.getMessage(messageId, messageArgs))
                        .build()
        );
        return json;
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
                        /* 입점몰에서 token response body 사용X - snlee.20220916
                        .uuid(RandomKey.getUUID())
                        .token(
                                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                                        .getRequest().getHeader(WBKey.Jwt.HeaderName)
                        )
                        */
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
