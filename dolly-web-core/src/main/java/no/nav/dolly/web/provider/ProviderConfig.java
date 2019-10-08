package no.nav.dolly.web.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class ProviderConfig implements WebMvcConfigurer {

    @Value("${dolly-web.security.cors.origins:''}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders(
                        "Access-Control-Allow-Headers",
                        "Access-Control-Request-Headers",
                        "Access-Control-Request-Method",
                        "X-Requested-With",
                        "X-XSRF-TOKEN",
                        "Content-Type"
                )
                .allowCredentials(true)
                .maxAge(3600L);
    }
}
