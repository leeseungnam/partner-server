package kr.wrightbrothers.apps.sign.service;

import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class WBUserDetailService implements UserDetailsService {

    private final SignService signService;
    private final String messagePrefix = "api.message.";
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public UserDetails loadUserByUsername(final String userId) throws UsernameNotFoundException {

        UserDetailDto user = signService.loadUserByUsername(userId);

        if(ObjectUtils.isEmpty(user)) throw new UsernameNotFoundException(messageSourceAccessor.getMessage(messagePrefix+"user.empty"));

        return UserPrincipal.createUser(user);
    }
}
