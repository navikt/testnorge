package no.nav.api.documentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Configure automated swagger API documentation
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Value("${application.version}")
	private String appVersion;
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(LocalDate.class, String.class)
				.directModelSubstitute(LocalDateTime.class, String.class)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.ant("/api/v1/**"))
				.build()
				.apiInfo(apiInfo());
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Dolly",
				"Dolly er en kloningsklient for NAVs personopplysning. Bruk den til å opprette fiktive testpersoner med fiktive inntekter og arbeidsforhold",
				""+appVersion,
				"https://nav.no",
				new Contact("Visma", "https://github.com/navikt/dolly", "nav.no"),
				"MIT",
				"https://github.com/navikt/dolly/blob/master/LICENSE"
		);
	}
}
