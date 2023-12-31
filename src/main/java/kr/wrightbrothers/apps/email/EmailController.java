package kr.wrightbrothers.apps.email;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"이메일"})
@Slf4j
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
public class EmailController extends WBController {

    private final EmailService emailService;

    @ApiOperation(value = "이메일 발송(단 건)", notes = "이메일 단 건 발송을 위한 API입니다.")
    @PostMapping("/single")
    public WBModel sendSingleEmail(@ApiParam(value = "메일 인증 요청 데이터") @Valid @RequestBody SingleEmailDto.ReqBody paramDto) {

        WBModel wbResponse = new WBModel();

        String authCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        paramDto.changeAuthCode(authCode);

        wbResponse.addObject("authEmail", emailService.singleSendEmail(paramDto));

        return  wbResponse;
    }
}
