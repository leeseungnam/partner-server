package kr.wrightbrothers.apps.user;

import io.swagger.annotations.*;
import kr.wrightbrothers.apps.common.annotation.UserPrincipalScope;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.*;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.user.dto.*;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.lang.WBCustomException;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api(tags = {"회원"})
@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController extends WBController {
    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 60 * 60 * 2;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final String messagePrefix = "api.message.";
    private final MessageSourceAccessor messageSourceAccessor;

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "회원가입", notes = "회원가입 요청 API 입니다.")
    @PostMapping()
    public WBModel insertUser(@ApiParam @Valid @RequestBody UserInsertDto paramDto) {
        if(!ObjectUtils.isEmpty(userService.findUserByDynamic(UserDto.builder().userId(paramDto.getUserId()).build()))) throw new WBCustomException(messagePrefix+"common.already.insert.custom"
                , new String[] {messageSourceAccessor.getMessage(messagePrefix+"word.email.address")});

        // encoding password
        paramDto.changePwd(passwordEncoder.encode(paramDto.getUserPwd()));
        //setUserStatusCode
        paramDto.changeUserStatusCode(User.Status.JOIN.getCode());

        userService.insertUser(paramDto);

        String [] messageArgs = {messageSourceAccessor.getMessage(messagePrefix+"word.signup")};
        return  defaultMsgResponse(messageSourceAccessor, "common.complete.custom", messageArgs);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "회원가입 이메일 인증", notes = "회원가입 이메일 인증 요청 API 입니다.")
    @PostMapping("/auth/email")
    public WBModel authEmail(@ApiParam(value = "메일 인증 요청 데이터") @Valid @RequestBody SingleEmailDto.ReqBody paramDto) {

        UserDto user = userService.findUserByDynamic(UserDto.builder().userId(paramDto.getUserId()).build());

        if(!ObjectUtils.isEmpty(user)) throw new WBCustomException(messagePrefix+"common.already.insert.user.custom", new String[] {paramDto.getUserId()});

        String authCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        paramDto.changeAuthCode(authCode);

        return  defaultResponse(emailService.singleSendEmail(paramDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경 API 입니다.")
    @PutMapping("/password")
    public WBModel updateUserPwd(@ApiParam @Valid @RequestBody UserPwdUpdateDto paramDto) {

        UserDto user = userService.findUserByDynamic(UserDto.builder().userId(paramDto.getUserId()).build());

        if(user.isChangePwdFlag())
            if(!userService.checkUserPasswordUpdate(paramDto.getUserId()))
                throw new WBCustomException(messagePrefix+"common.expired");

        // encoding password
        paramDto.changePwd(passwordEncoder.encode(paramDto.getUserPwd()));
        paramDto.setChangePwdFlag(false);

        userService.updateUserPwd(paramDto);

        return  defaultMsgResponse(messageSourceAccessor, "user.update.password.success", null);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "아이디 찾기", notes = "아이디를 찾기 위한 API 입니다.")
    @PostMapping("/search/id")
    public WBModel findUserId(@ApiParam @Valid @RequestBody UserIdFindDto.ReqBody paramDto) {

        WBModel response = new WBModel();

        UserDto userDto = userService.findUserByDynamic(UserDto.builder()
                .userName(paramDto.getUserName())
                .userPhone(paramDto.getUserPhone())
                .build());

        if(ObjectUtils.isEmpty(userDto)) throw new WBCustomException(ErrorCode.UNAUTHORIZED, messagePrefix+"user.unknown", null);

        response.addObject("userId", userDto.getUserId());

        Object[] messageArgs = {userDto.getUserId()};
        response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(messagePrefix+"search.id.success", messageArgs));

        return response;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "비밀번호 찾기", notes = "비밀번호를 찾기 위한 API 입니다.")
    @PostMapping("/search/pwd")
    public WBModel findUserPwd(@ApiParam @Valid @RequestBody UserPwdFindDto paramDto) throws Exception{

        WBModel response = new WBModel();

        UserDto userDto = userService.findUserByDynamic(UserDto.builder()
                .userId(paramDto.getSingleEmail().getUserId())
                .userName(paramDto.getUserName())
                .userPhone(paramDto.getUserPhone())
                .build());

        if(ObjectUtils.isEmpty(userDto)) throw new WBCustomException(ErrorCode.UNAUTHORIZED, messagePrefix+"user.unknown", null);

        String authCode = RandomStringUtils.randomAlphanumeric(9).toUpperCase();
        authCode += RandomUtil.getSpCha(new char[] {'!','@','#','$','%','^','&','*','(',')'}, 1);

        userDto.changePwd(passwordEncoder.encode(authCode));

        //transaction
        response.addObject("authEmail", userService.findUserPwd(userDto, SingleEmailDto.ReqBody.builder()
                .userId(paramDto.getSingleEmail().getUserId())
                .userName(paramDto.getUserName())
                .authCode(authCode)
                .emailType(paramDto.getSingleEmail().getEmailType())
                .build()));

        Object [] messageArgs = {userDto.getUserId()};
        response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(messagePrefix+"email.password.success", messageArgs));

        return  response;
    }

    @UserPrincipalScope
    @ApiImplicitParams({
            @ApiImplicitParam(name = PartnerKey.Jwt.Header.AUTHORIZATION, value = PartnerKey.Jwt.Alias.ACCESS_TOKEN, required = true, dataType = "string", dataTypeClass = String.class, paramType = "header")
    })
    @ApiOperation(value = "인가 정보 변경", notes = "인가(권한) 정보를 변경하기 위한 API 입니다.")
    @PostMapping("/auth")
    public WBModel setAuthentic(@RequestHeader(PartnerKey.Jwt.Header.AUTHORIZATION) String accessToken
            , @CookieValue(PartnerKey.Jwt.Alias.REFRESH_TOKEN) String refreshToke
            , @ApiIgnore @AuthenticationPrincipal UserPrincipal userPrincipal
            , @ApiParam  @Valid @RequestBody UserAuthDto userAuth
            , HttpServletResponse response
    ) {
        // select userAuth
        if(!userService.checkAuth(userAuth)) throw new WBCustomException(ErrorCode.FORBIDDEN, messagePrefix+"common.forbidden", null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updateAuthorities = new ArrayList<>();
        updateAuthorities.add(new SimpleGrantedAuthority(userAuth.getAuthCode()));

        userPrincipal.changeUserAuth(userAuth);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(userPrincipal, authentication.getCredentials(), updateAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        String newAccessToken = jwtTokenProvider.generateAccessToken(newAuth);
        String newRefreshToken = jwtTokenProvider.issueRefreshToken(authentication);

        log.info("ACCESS_TOKEN[OLD]::{}",accessToken);
        log.info("ACCESS_TOKEN[NEW]::{}",newAccessToken);

        log.info("REFRESH_TOKEN[OLD]::{}",refreshToke);
        log.info("REFRESH_TOKEN[NEW]::{}",newRefreshToken);

        log.info(userPrincipal.getUsername());

        if(!ObjectUtils.isEmpty(userPrincipal.getUserAuth())){
            log.info("UserAuth::{},{}",userPrincipal.getUserAuth().getAuthCode(), userPrincipal.getUserAuth().getPartnerCode());
        }else{
            log.info("UserAuth is null");
        }

        response.setHeader(PartnerKey.Jwt.Header.AUTHORIZATION, PartnerKey.Jwt.Type.BEARER + newAccessToken);
        response.addCookie(TokenUtil.createCookie(PartnerKey.Jwt.Alias.REFRESH_TOKEN, newRefreshToken, REFRESH_TOKEN_VALIDATION_SECOND));

        return noneDataResponse();
    }
}
