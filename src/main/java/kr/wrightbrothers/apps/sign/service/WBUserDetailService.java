package kr.wrightbrothers.apps.sign.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WBUserDetailService implements UserDetailsService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.sign.query.Sign.";
//    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String userId) {
        UserDetailDto user = dao.selectOne(namespace + "loadUserByUsername", userId);
//        UserDto user = userService.findById(userId);

        // [todo] AbstractUserDetailsauthenticationProvider 분리
        if(ObjectUtils.isEmpty(user)) throw new WBBusinessException(403, "not found user");
        return createUser(user);
    }

    private User createUser(UserDetailDto userDto) {
        List<GrantedAuthority> grantedAuthorities = userDto.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthCode()))
                .collect(Collectors.toList());

        return new User(userDto.getUserId(), userDto.getUserPwd(), grantedAuthorities);
    }
}
