package kr.wrightbrothers.apps.common.config.security;

import kr.wrightbrothers.apps.common.config.security.jwt.JwtAccessDeniedHandler;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtAuthenticationEntryPoint;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtSecurityConfiguration;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.service.SignService;
import kr.wrightbrothers.apps.token.service.BlackListService;
import kr.wrightbrothers.apps.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final SignService signService;
    private final BlackListService blackListService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/resources/**",
                "/docs/**",                 // RestDocs
                "/swagger-resources/**",    // Swagger
                "/swagger-ui/**",         // Swagger
                "/v2/api-docs/**",          // Swagger
                "/swagger/**"               // Swagger
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                .antMatchers("/v1/sign/login","/v1/sign/logout/response"
                        , "/v1/auth/email")
                .permitAll()
                .anyRequest().authenticated()
            .and()
//                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
                .apply(new JwtSecurityConfiguration(signService, jwtTokenProvider));
        http.logout()
//                .logoutUrl("/v1/sign/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/v1/sign/logout"))
                .addLogoutHandler(new WBLogoutHandler(jwtTokenProvider, blackListService, refreshTokenService))
                .logoutSuccessUrl("/v1/sign/logout/response")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies(PartnerKey.Jwt.Alias.REFRESH_TOKEN);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:3300", "http://localhost:3200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-disposition", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
