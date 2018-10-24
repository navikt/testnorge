package no.nav.identpool;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import com.google.common.base.Predicate;

import lombok.RequiredArgsConstructor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Environment environment;

    @Bean
    public Docket personFeedApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("ident-pool")
                .apiInfo(apiInfo())
                .tags(new Tag("identifikator", "Endepunkter for å rekvirere test-identer"),
                        new Tag("finnes hos skatt", "DREK sitt endepunkt for rekvirering av DNR fra SKD"))
                .select()
                .paths(path())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Ident-pool API")
                .description("https://confluence.adeo.no/display/FEL/Ident-pool")
                .version(environment.getProperty("application.version"))
                .build();
    }

    private Predicate<String> path() {
        return regex("/api.*");
    }
}