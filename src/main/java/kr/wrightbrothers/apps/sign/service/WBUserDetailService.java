package kr.wrightbrothers.apps.sign.service;

import kr.wrightbrothers.apps.sign.dto.UserDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WBUserDetailService implements UserDetailsService {

    private final WBCommonDao dao;

    @Override
    public UserDetails loadUserByUsername(final String usrId) {
        UserDto user = dao.selectOne("kr.wrightbrothers.apps.sign.query.Sign.loadUserByUsername", usrId);
        return createUser(user);
    }

    private User createUser(UserDto userDto) {
        List<GrantedAuthority> grantedAuthorities = userDto.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getAuthCode()))
                .collect(Collectors.toList());

        return new User(userDto.getUserId(), userDto.getUserPw(), grantedAuthorities);
    }
}
