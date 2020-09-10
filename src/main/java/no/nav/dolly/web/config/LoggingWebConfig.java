package no.nav.dolly.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import no.nav.dolly.web.logging.LogApiRequestInterceptor;

@Configuration
public class LoggingWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogApiRequestInterceptor());
    }
}