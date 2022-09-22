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

    private final SignService signService;

    @Override
    public UserDetails loadUserByUsername(final String userId) throws UsernameNotFoundException {

        UserDetailDto user = signService.loadUserByUsername(userId);

        if(ObjectUtils.isEmpty(user)) throw new UsernameNotFoundException("입력하신 이메일주소 및 비밀번호를 확인해주세요.");

        if(PartnerKey.Code.User.Status.DROP_REQUEST.equals(user.getUserStatusCode())) {
            throw new UsernameNotFoundException("탈퇴 진행 중인 사용자 입니다.");
        } else if(PartnerKey.Code.User.Status.DROP_COMPLETE.equals(user.getUserStatusCode())) {
            throw new UsernameNotFoundException("탈퇴한 파트너 센터 아이디 입니다.");
        }
        return UserPrincipal.createUser(user);
    }
}
