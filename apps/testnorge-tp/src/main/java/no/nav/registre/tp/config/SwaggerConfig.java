package no.nav.registre.tp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.HashSet;
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
        Set<String> contentTypeJson = new HashSet<>(Collections.singletonList("application/json"));
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(ApiIgnore.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(apiInfo())
                .produces(contentTypeJson)
                .consumes(contentTypeJson)
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Testnorge-TP",
                "Testnorge-TP er integrasjonen mellom Orkestratoren og TJPEN databasen. Testnorge-TP går mot TJPEN i gitte miljøer",
                "" + appVersion,
                "https://nav.no",
                new Contact("Fellesregistrene på NAV", "https://github.com/navikt/testnorge-tp", null),
                "Super Strict Licence",
                "https://opensource.org/licenses/super-strict-license",
                Collections.emptyList()
        );
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }
}
