package no.nav.dolly.provider.api.documentation;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static springfox.documentation.builders.PathSelectors.ant;
import static springfox.documentation.builders.RequestHandlerSelectors.any;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * Configure automated swagger API documentation
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String PARAM_TYPE = "header";
    private static final String MODEL_TYPE_STRING = "string";

    @Value("${dolly.api.v1.name}")
    private String apiV1Name;

    @Value("${dolly.api.v1.description}")
    private String apiV1Description;

    @Value("${dolly.api.v1.header.nav-consumer-id}")
    private String apiV1ConsumerIdDescription;

    @Value("${dolly.api.v1.header.nav-call-id}")
    private String apiV1CallIdDescription;


    @Bean
    public Docket v1ApiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfo(apiV1Name, apiV1Description, null, null, null, null, null, emptyList()))
                .select().apis(any()).paths(ant("/api/v1/**")).build()
                .globalOperationParameters(globalHeaders());
    }

    @Bean
    UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder()
                .docExpansion(DocExpansion.LIST)
                .displayRequestDuration(true)
                .validatorUrl(null)
                .build();
    }

    private List<Parameter> globalHeaders() {
        return asList(
                new ParameterBuilder()
                        .name(AUTHORIZATION)
                        .description("\"Bearer \" + OIDC token")
                        .modelRef(new ModelRef(MODEL_TYPE_STRING))
                        .parameterType(PARAM_TYPE)
                        .required(true)
                        .build(),
                new ParameterBuilder()
                        .name(HEADER_NAV_CONSUMER_ID)
                        .description(apiV1ConsumerIdDescription)
                        .modelRef(new ModelRef(MODEL_TYPE_STRING))
                        .parameterType(PARAM_TYPE)
                        .required(true)
                        .build(),
                new ParameterBuilder()
                        .name(HEADER_NAV_CALL_ID)
                        .description(apiV1CallIdDescription)
                        .modelRef(new ModelRef(MODEL_TYPE_STRING))
                        .parameterType(PARAM_TYPE)
                        .required(true)
                        .build());
    }
}
