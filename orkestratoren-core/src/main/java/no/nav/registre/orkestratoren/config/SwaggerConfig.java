package no.nav.registre.orkestratoren.config;

import java.util.Arrays;
import java.util.HashSet;
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
              HashSet contentTypeJson = new HashSet(Arrays.asList("application/json"));
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
                "Orkestratoren",
                "Testnorges Orkestrator bestiller generering av syntetiske meldinger i de fellesregistrenes mange synt-pakker, "
                        + "slik at de blir tilgjengelige i registrene (eller deres stub-versjon) i det miljøet som denne orkestratoren er konfigurert for.",
                "" + appVersion,
                "https://nav.no",
                new Contact("Fellesregistrene på NAV", "http://stash.devillo.no/projects/FEL/repos/orkestratoren/browse", null),
                "Super Strict Licence",
                "https://opensource.org/licenses/super-strict-license"
        );
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }
}
