package no.nav.identpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.HashSet;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig implements WebMvcConfigurer {

    private final Environment environment;

    @Bean
    public Docket api() {
        HashSet<String> contentTypeJson = new HashSet<>(Collections.singletonList("application/json"));
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("ident-pool")
                .apiInfo(apiInfo())
                .tags(new Tag("identifikator", "Endepunkter for test-identer"),
                        new Tag("fiktive navn", "Endepunkter for fiktive navn"),
                        new Tag("finnes hos skatt", "DREK sitt endepunkt for rekvirering av DNR fra SKD"))
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .produces(contentTypeJson)
                .consumes(contentTypeJson)
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Ident-pool API")
                .description("https://confluence.adeo.no/display/FEL/Ident-pool")
                .version(environment.getProperty("application.version"))
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }
}