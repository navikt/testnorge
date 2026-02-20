package no.nav.dolly.budpro;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
class OpenApiConfig implements WebFilter {

    @Bean
    OpenAPI openAPI() {
        final String authBearer = "Authorization: Bearer";
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(authBearer, Arrays.asList("read", "write")))
                .components(
                        new Components()
                                .addSecuritySchemes(authBearer,
                                        new SecurityScheme()
                                                .description("If running locally, use https://testnav-oversikt.intern.dev.nav.no/magic-token")
                                                .name(authBearer)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
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
