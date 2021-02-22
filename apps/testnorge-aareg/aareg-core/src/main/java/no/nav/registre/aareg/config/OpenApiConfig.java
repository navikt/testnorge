package no.nav.registre.aareg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

/**
 * Configure automated swagger API documentation
 */

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    private static final String HEADER = "header";

    @Value("${application.version}")
    private String appVersion;

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("testnorge-aareg")
                        .description("testnorge-aareg legger syntetiske arbeidsforhold og arbeidsforhold fra dolly inn i aareg.")
                        .version(appVersion)
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
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> operation
                .addParametersItem(new Parameter()
                        .in(HEADER)
                        .required(true)
                        .description("En ID for systemet som gjør kallet, som regel servicebrukeren til applikasjonen.")
                        .name(HEADER_NAV_CONSUMER_ID)
                        .example("MinApp"))
                .addParametersItem(new Parameter()
                        .in(HEADER)
                        .required(true)
                        .description("En ID som identifiserer kallkjeden som dette kallet er en del av.")
                        .name(HEADER_NAV_CALL_ID)
                        .example("123e4567-e89b-12d3-a456-426614174000"));
    }
}
