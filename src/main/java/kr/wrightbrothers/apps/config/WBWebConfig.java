package kr.wrightbrothers.apps.config;

import kr.wrightbrothers.apps.config.interceptor.WBInterceptor;
import kr.wrightbrothers.framework.support.WBKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
@RequiredArgsConstructor
public class WBWebConfig implements WebMvcConfigurer {

    private final WBInterceptor wbInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(wbInterceptor).addPathPatterns("/**/");
    }

    @Bean(name = WBKey.View)
    public MappingJackson2JsonView JSON() {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        return view;
    }

}
