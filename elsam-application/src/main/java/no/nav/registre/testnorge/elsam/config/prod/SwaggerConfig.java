package no.nav.registre.testnorge.elsam.config.prod;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.Set;

/**
 * Configure automated swagger API documentation
 */

@Profile("prod")
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${application.version}")
    private String appVersion;

    @Bean
    public Docket api() {
        Set<String> applicationType = Collections.singleton("application/json");
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(ApiIgnore.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(apiInfo())
                .produces(applicationType)
                .consumes(applicationType)
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("testnorge-elsam")
                .description("Applikasjon som håndterer integrasjon mellom syfo og resten av synt/dolly-stacken.")
                .version(appVersion)
                .termsOfServiceUrl("https://nav.no")
                .contact(new Contact("Fellesregistrene på NAV", "http://stash.devillo.no/projects/FEL/repos/testnorge-elsam/browse", null))
                .license("Super Strict Licence")
                .licenseUrl("https://opensource.org/licenses/super-strict-license")
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }
}
