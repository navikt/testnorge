package no.nav.testnav.identpool.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    private static final String BEARER_AUTH = "bearerAuth";
    private static final String USER_JWT = "user-jwt";

    @Bean
    public OpenAPI openApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("Ident-Pool API")
                        .version("Versjon 1")
                        .description("Ident-pool har oversikt på syntetiske identer, og sjekker mot prod og testmiljøer for eksistens.<br>"+
                                "Fra ident-pool kan det rekvireres identer basert på født-før og født-etter samt kjønn og type.<br>" +
                                "Spesifikke identer kan også allokeres.<br>" +
                                "Identer kan frigjøres og benyttes om igjen")
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
                                .addList(BEARER_AUTH, Arrays.asList("read", "write"))
                                .addList(USER_JWT, Arrays.asList("read", "write"))
                )
                .components(
                        new Components()
                                .addSecuritySchemes(BEARER_AUTH,
                                        new SecurityScheme()
                                                .description("Legg inn token kun, uten \"Bearer \"")
                                                .name(BEARER_AUTH)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                                .addSecuritySchemes(USER_JWT,
                                        new SecurityScheme()
                                                .name(UserConstant.USER_HEADER_JWT)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui.html");
    }
}