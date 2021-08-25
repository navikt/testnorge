package no.nav.registre.aareg.config.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ProviderConfig implements WebMvcConfigurer {

    @Value("${testnorge.aareg.security.cors.origins:''}")
    private String[] allowedOrigins;

    @Autowired
    private final RestMdcInterceptor restMdcInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restMdcInterceptor)
                .excludePathPatterns("/api", "/api/v1/syntetisering/**", "/api/v1/ident/**")
                .addPathPatterns("/api/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }

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
