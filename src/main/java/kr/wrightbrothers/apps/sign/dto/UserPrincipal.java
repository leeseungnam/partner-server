package kr.wrightbrothers.apps.sign.dto;

import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.apps.user.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private String username;

    private String password;

    private UserAuthDto userAuth;

    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UserDetailDto userDetailDto, Collection<? extends GrantedAuthority> authorities) {
        this.username = userDetailDto.getUserId();
        this.password = userDetailDto.getUserPwd();
        this.userAuth = userDetailDto.getUserAuth();
        this.authorities = authorities;
    }

    public static UserPrincipal createUser(UserDetailDto userDetailDto) {
        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority(userDetailDto.getUserAuth().getAuthCode()));

        return new UserPrincipal(userDetailDto, grantedAuthorities);
    }

    public static UserPrincipal modifyUser(UserDetailDto userDetailDto) {
        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority(userDetailDto.getUserAuth().getAuthCode()));

        return new UserPrincipal(userDetailDto, grantedAuthorities);
    }

    public UserAuthDto getUserAuth() {
        return userAuth;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
