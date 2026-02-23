package no.nav.registre.testnorge.profil.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletcore.config.ApplicationProperties;

@Configuration
@Import(ApplicationProperties.class)
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI openApi(ApplicationProperties applicationProperties) {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name(HttpHeaders.AUTHORIZATION)
                        )
                        .addSecuritySchemes("user-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name(UserConstant.USER_HEADER_JWT)
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearer-jwt", Arrays.asList("read", "write"))
                                .addList("user-jwt", Arrays.asList("read", "write"))
                )
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
                        ));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui.html");
    }
}