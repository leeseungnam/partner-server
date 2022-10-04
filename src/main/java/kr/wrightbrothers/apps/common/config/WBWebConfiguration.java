package kr.wrightbrothers.apps.common.config;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.interceptor.WBInterceptor;
import kr.wrightbrothers.framework.support.WBKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class WBWebConfiguration implements WebMvcConfigurer {

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

    @Value("${system.lang}")
    private String lang;

    @Bean(name = "localeResolver")
    public FixedLocaleResolver staticLocaleResolver(){
        FixedLocaleResolver fixedLocaleResolver = new FixedLocaleResolver();

        if(PartnerKey.WBConfig.Message.LANG_KO.equals(lang)){
            fixedLocaleResolver.setDefaultLocale(Locale.KOREAN);
        }else if(PartnerKey.WBConfig.Message.LANG_EN.equals(lang)){
            fixedLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        }else if(PartnerKey.WBConfig.Message.LANG_JA.equals(lang)){
            fixedLocaleResolver.setDefaultLocale(Locale.JAPANESE);
        }else{
            fixedLocaleResolver.setDefaultLocale(Locale.KOREAN);
        }
        return fixedLocaleResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:messages/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(60);

        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource());

        return messageSourceAccessor;
    }

}
