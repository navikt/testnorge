package no.nav.registre.testnorge.libs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import no.nav.registre.testnorge.libs.logging.LogApiRequestInterceptor;

@Configuration
public class LoggingWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogApiRequestInterceptor());
    }
}