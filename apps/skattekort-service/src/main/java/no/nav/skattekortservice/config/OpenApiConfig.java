package no.nav.skattekortservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import no.nav.testnav.libs.reactivecore.config.ApplicationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
public class OpenApiConfig implements WebFilter {

    @Bean
    public OpenAPI openApi(ApplicationProperties applicationProperties) {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name(HttpHeaders.AUTHORIZATION)
                ))
                .addSecurityItem(
                        new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")))
                .info(new Info()
                        .title(applicationProperties.getName())
                        .version(applicationProperties.getVersion())
                        .description(applicationProperties.getDescription())
                        .termsOfService("https://nav.no")
                        .contact(new Contact()
                                .url("https://nav-it.slack.com/archives/CA3P9NGA2")
                                .email("dolly@nav.no")
                                .name("Team Dolly")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().equals("/swagger")) {
            return chain
                    .filter(exchange.mutate()
                            .request(exchange.getRequest()
                                    .mutate().path("/swagger-ui.html").build())
                            .build());
        }

        return chain.filter(exchange);
    }
}