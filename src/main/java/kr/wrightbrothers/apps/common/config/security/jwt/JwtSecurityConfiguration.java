package kr.wrightbrothers.apps.common.config.security.jwt;

import kr.wrightbrothers.apps.sign.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final SignService signService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configure(HttpSecurity httpSecurity) {
        JwtTokenFilter filter = new JwtTokenFilter(signService, jwtTokenProvider);
        httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
