package no.nav.registre.hodejegeren.provider.rs;

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
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(ApiIgnore.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Testnorge-Hodejegeren",
                "Testnorge-Hodejegeren henter syntetiserte meldinger fra TPS Syntetisereren og jakter på hoder/testpersoner i "
                        + "TPSF og TPS i testmiljø for å fylle meldingene med identer som oppfyller visse kriterier. ",
                "" + appVersion,
                "https://nav.no",
                new Contact("NAV", "http://stash.devillo.no/projects/FEL/repos/testnorge-hodejegeren/browse", "nav.no"),
                "Super Strict Licence",
                "https://opensource.org/licenses/super-strict-license"
        );
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }
}
