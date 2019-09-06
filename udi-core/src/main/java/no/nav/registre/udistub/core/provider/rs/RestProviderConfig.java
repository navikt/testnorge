package no.nav.registre.udistub.core.provider.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RestProviderConfig implements WebMvcConfigurer {

    @Value("${udistub.web.api.cors.origins:''}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/api/v1/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("OPTIONS")
                .allowCredentials(true);
    }
}