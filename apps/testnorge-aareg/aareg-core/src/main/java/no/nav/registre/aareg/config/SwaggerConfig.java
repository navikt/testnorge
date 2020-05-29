package no.nav.registre.aareg.config;

import static java.util.Arrays.asList;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.registre.aareg.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Configure automated swagger API documentation
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    private static final String PARAM_TYPE = "header";
    private static final String MODEL_TYPE_STRING = "string";

    @Value("${application.version}")
    private String appVersion;

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket api() {
        HashSet contentTypeJson = new HashSet(Arrays.asList("application/json"));
        return new Docket(DocumentationType.SWAGGER_2)
                .alternateTypeRules(
                        AlternateTypeRules.newRule(
                                typeResolver.resolve(ResponseEntity.class,
                                        typeResolver.resolve(List.class, typeResolver.resolve(Map.class))),
                                typeResolver.resolve(ResponseEntity.class),
                                Ordered.HIGHEST_PRECEDENCE
                        ))
                .ignoredParameterTypes(ApiIgnore.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(apiInfo())
                .produces(contentTypeJson)
                .consumes(contentTypeJson)
                .globalOperationParameters(globalHeaders())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("testnorge-aareg")
                .description("testnorge-aareg legger syntetiske arbeidsforhold og arbeidsforhold fra dolly inn i aareg.")
                .version(appVersion)
                .termsOfServiceUrl("https://nav.no")
                .contact(new Contact("Fellesregistrene på NAV", "http://stash.devillo.no/projects/FEL/repos/testnorge-aareg/browse", null))
                .license("Super Strict Licence")
                .licenseUrl("https://opensource.org/licenses/super-strict-license")
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
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
                        .description("En ID for systemet som gjør kallet, som regel servicebrukeren til applikasjonen.")
                        .modelRef(new ModelRef(MODEL_TYPE_STRING))
                        .parameterType(PARAM_TYPE)
                        .required(true)
                        .build(),
                new ParameterBuilder()
                        .name(HEADER_NAV_CALL_ID)
                        .description("En ID som identifiserer kallkjeden som dette kallet er en del av.")
                        .modelRef(new ModelRef(MODEL_TYPE_STRING))
                        .parameterType(PARAM_TYPE)
                        .required(true)
                        .build());
    }
}
