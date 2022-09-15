package kr.wrightbrothers.apps.sign.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import software.amazon.awssdk.services.connect.model.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WBUserDetailService implements UserDetailsService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.sign.query.Sign.";

    @Override
    public UserDetails loadUserByUsername(final String userId) throws UsernameNotFoundException {
        UserDetailDto user = dao.selectOne(namespace + "loadUserByUsername", userId);

        // [todo] AbstractUserDetailsauthenticationProvider 분리
        if(ObjectUtils.isEmpty(user)) throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        return UserPrincipal.createUser(user);
    }
}
