package kr.wrightbrothers.apps.noti;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.constants.Notification;
import kr.wrightbrothers.apps.common.constants.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.noti.dto.AuthPhoneDto;
import kr.wrightbrothers.apps.queue.NotificationQueue;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"NOTI"})
@Slf4j
@RestController()
@RequestMapping(value = "/v1/noti")
@RequiredArgsConstructor
public class NotificationController extends WBController {

    private final NotificationQueue notificationQueue;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "휴대폰번호 인증", notes = "휴대폰번호 인증 요청 API 입니다.")
    @PostMapping("/auth/phone")
    public WBModel authPhone(@ApiParam(value = "휴대폰번호 인증 요청 데이터") @Valid @RequestBody AuthPhoneDto.ReqBody paramDto) {

        String authCode = RandomUtil.generateNumeric(6);
        String text = Notification.AUTH_PHONE.getMessageText();
        text = text.replaceAll("#\\{authCode\\}", authCode);

        notificationQueue.sendToAdmin(DocumentSNS.NOTI_SMS_SINGLE, Notification.AUTH_PHONE, paramDto.getPhone(), text);

        return defaultResponse(AuthPhoneDto.ResBody.builder().phone(paramDto.getPhone()).authCode(authCode).build());
    }
}
