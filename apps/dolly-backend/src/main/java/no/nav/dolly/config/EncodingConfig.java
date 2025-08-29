package no.nav.dolly.config;

import org.apache.commons.codec.CharEncoding;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class EncodingConfig {

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {

        var characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(CharEncoding.UTF_8);
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> registrationBean() {

        var registrationBean = new FilterRegistrationBean<CharacterEncodingFilter>();
        registrationBean.setFilter(characterEncodingFilter());
        return registrationBean;
    }
}