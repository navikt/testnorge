package no.nav.registre.testnorge.synt.person.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Configure automated swagger API documentation
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${application.version}")
    private String appVersion;

    @Bean
    public Docket api() {
        Set<String> contentTypeJson = new HashSet<>(Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(ApiIgnore.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .securitySchemes(securitySchemes())
                .apiInfo(apiInfo())
                .produces(contentTypeJson)
                .consumes(contentTypeJson)
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Testnorge Synt Person API")
                .description("API for syntetisering av personer")
                .version(appVersion)
                .termsOfServiceUrl("https://nav.no")
                .contact(new Contact("Team Dolly", "https://nav-it.slack.com/archives/CA3P9NGA2", "dolly@nav.no"))
                .build();
    }

    private static List<? extends SecurityScheme> securitySchemes() {
        return Collections.singletonList(new ApiKey("Bearer", "Authorization", "header"));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui.html");
    }
}