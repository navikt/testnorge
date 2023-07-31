package no.nav.dolly.budpro;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    OpenAPI openAPI() {
        final String authBearer = "Authorization: Bearer";
        final String userJwt = "user-jwt";

        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(authBearer, Arrays.asList("read", "write")))
                .components(
                        new Components()
                                .addSecuritySchemes(authBearer,
                                        new SecurityScheme()
                                                .description("Token from https://testnav-oversikt.intern.dev.nav.no/magic-token")
                                                .name(authBearer)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui.html");
    }

}
