package kr.wrightbrothers.apps.sign.service;

import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("authenticationProvider")
public class WBAuthenticationProvider implements AuthenticationProvider {

    private String MESSAGE_PREFIX="api.message.";
    private final MessageSourceAccessor messageSourceAccessor;
    private final PasswordEncoder passwordEncoder;
    private final WBUserDetailService wbUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String loginId = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserPrincipal user = (UserPrincipal) wbUserDetailService.loadUserByUsername(loginId);

        if (isNotMatches(password, user.getPassword())) {
            throw new BadCredentialsException(loginId);
        }
        log.info("[authenticate]::userStatus={}",user.getUserStatusCode());
        if(User.Status.DROP_REQUEST.getCode().equals(user.getUserStatusCode())
                || User.Status.DROP_COMPLETE.getCode().equals(user.getUserStatusCode())) {
            throw new LockedException(messageSourceAccessor.getMessage(MESSAGE_PREFIX+"user.comment."+ user.getUserStatusCode()));
        }
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean isNotMatches(String password, String encodePassword) {
        return !passwordEncoder.matches(password, encodePassword);
    }
}
