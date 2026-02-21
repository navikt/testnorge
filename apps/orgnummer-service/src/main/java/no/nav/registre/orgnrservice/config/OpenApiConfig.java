package no.nav.registre.orgnrservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletcore.config.ApplicationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI openApi(ApplicationProperties applicationProperties) {

        final String bearerAuth = "bearerAuth";
        final String userJwt = "user-jwt";
        return new OpenAPI()
                .info(new Info()
                        .title(applicationProperties.getName())
                        .version(applicationProperties.getVersion())
                        .description(applicationProperties.getDescription())
                        .termsOfService("https://nav.no")
                        .contact(new Contact()
                                .url("https://nav-it.slack.com/archives/CA3P9NGA2")
                                .email("dolly@nav.no")
                                .name("Team Dolly")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(userJwt, Arrays.asList("read", "write"))
                                .addList(bearerAuth, Arrays.asList("read", "write"))
                )
                .components(
                        new Components()
                                .addSecuritySchemes(bearerAuth,
                                        new SecurityScheme()
                                                .description("Legg inn token kun, uten \"Bearer \"")
                                                .name(bearerAuth)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                                .addSecuritySchemes(userJwt,
                                        new SecurityScheme()
                                                .name(UserConstant.USER_HEADER_JWT)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                );
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui.html");
    }
}