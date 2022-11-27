package kr.wrightbrothers.apps.aop;

import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.queue.NotificationQueue;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class UserAfterAop {
    private final NotificationQueue notificationQueue;

    private final UserService userService;
    /**
     * <pre>
     *     회원 등록, 패스워드 변경 시 userId 로회원 정보를 조회하여  Admin 2.0 API 서버에 Notificat
     *     ion SEND 요청합니다.
     *
     *     수신자 : 회원가입 시 입력 받은 휴대폰 번호
     *
     * </pre>
     */
    //  send to admin
    @AfterReturning(value = "execution(* kr.wrightbrothers.apps.user.UserController.insertUser(..)) ||"
            +"execution(* kr.wrightbrothers.apps.user.UserController.updateUserPwd(..))"
    )
    public void sendUserNotificationSnsData(JoinPoint joinPoint) throws Exception {
        log.info("[sendUserNotification]::Send User Noti ... START");

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        JSONObject object = new JSONObject(JsonUtil.ToString(Arrays.stream(joinPoint.getArgs()).findFirst().orElseThrow()));

        Notification notification = Notification.NULL;

        if(object.has("userId")) {
            log.info("[sendUserNotification]::userId={}", object.getString("userId"));

            // find user
            UserDto user = userService.findUserByDynamic(UserDto.builder().userId(object.getString("userId")).build());

            notification = methodSignature.getMethod().getName().contains("insert") ? Notification.SIGN_UP : Notification.CHANGE_PASSWORD;

            notificationQueue.sendPushToAdmin(DocumentSNS.NOTI_KAKAO_SINGLE
                    , notification
                    , user.getUserPhone()
                    , methodSignature.getMethod().getName().contains("insert") ? new String[] {user.getUserName()} : null
            );
        }
        log.info("[sendPartnerSnsDataByUpdateThumbnail]::USER Send NOTI ... END");
    }
}
