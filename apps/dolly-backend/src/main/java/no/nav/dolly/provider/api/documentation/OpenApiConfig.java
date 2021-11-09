package no.nav.dolly.provider.api.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    private static final String HEADER = "header";

    @Value("${dolly.api.v1.name}")
    private String apiV1Name;

    @Value("${dolly.api.v1.description}")
    private String apiV1Description;

    @Value("${dolly.api.v1.version}")
    private String appVersion;

    @Value("${dolly.api.v1.header.nav-consumer-id}")
    private String apiV1ConsumerIdDescription;

    @Value("${dolly.api.v1.header.nav-call-id}")
    private String apiV1CallIdDescription;

    @Bean
    public OpenAPI openApi() {
        final String bearerAuth = "bearerAuth";
        final String userJwt = "user-jwt";
        final String apiTitle = String.format("%s API", StringUtils.capitalize(apiV1Name));

        return new OpenAPI()
                .info(new Info()
                        .title(apiTitle)
                        .version(appVersion)
                        .description(apiV1Description)
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
                                .addList(bearerAuth, Arrays.asList("read", "write"))
                                .addList(userJwt, Arrays.asList("read", "write"))
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
                                                .bearerFormat("JWT")
                                )
                );
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> operation
                .addParametersItem(new Parameter()
                        .in(HEADER)
                        .required(true)
                        .description(apiV1ConsumerIdDescription)
                        .name(HEADER_NAV_CONSUMER_ID)
                        .example("MinApp"))
                .addParametersItem(new Parameter()
                        .in(HEADER)
                        .required(true)
                        .description(apiV1CallIdDescription)
                        .name(HEADER_NAV_CALL_ID)
                        .example("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui/index.html?url=/v3/api-docs/");
    }
}