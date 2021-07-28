package no.nav.registre.syntrest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${application.version}")
    private String appVersion;

    @Bean
    public Docket api() {
        var contentTypeJson = new HashSet(Collections.singletonList("application/json"));
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
        return new ApiInfoBuilder()
                .title("SyntRest")
                .description("API for å generere syntetisk data fra synth-pakkene\n\nMange av maskinlærings-modellene for " +
                        "syntetisering av ulike miljøer tar opptil flere Gb med lagringsplass. Dette har vist seg vanskelig " +
                        "å senke, siden det i BeAn brukes tredjeparts-biblioteker (scikit-learn) med ferdige moduler som tar " +
                        "opp plass. \n\nNår en applikasjon deployes på kubernetes-clusteret til NAIS spesifiserer man minimum " +
                        "påkrevd minne, slik at applikasjonen alltid vil kreve minst så mye ressurser av clusteret så lenge " +
                        "den kjører. Siden generering av syntetiske testdata kun skjer når det blir gjort et kall til en " +
                        "synt-applikasjon, er det klart at disse synt-appliakasjonene ikke trenger å være oppe og kjøre på " +
                        "clusteret hele tiden (og ta opp unødvendige ressurser). \n\nLøsningen på dette problemet er en egen " +
                        "SyntRest-applikasjon som dynamisk deployer og fjerner synt-applikasjoner \"on the fly\" etter behov, " +
                        "i tillegg til å være applikasjonen man gjør alle api-kall til, siden de blir routet videre til " +
                        "endepunkter i synt-applikasjonene.")
                .version("" + appVersion)
                .termsOfServiceUrl("https://nav.no")
                .contact(new Contact("Fellesregistrene på NAV", "http://stash.devillo.no/projects/TREGPOCS/repos/syntrest/browse", null))
                .license("Super Strict Licence")
                .licenseUrl("https://opensource.org/licenses/super-strict-license")
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api").setViewName("redirect:/swagger-ui.html");
    }
}