package no.nav.dolly.budpro;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;
import java.util.Arrays;

@Configuration
class OpenApiConfig {

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

    @Bean
    RouterFunction<ServerResponse> swaggerRedirect() {
        return RouterFunctions
                .route()
                .GET("/swagger", request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build())
                .build();
    }

}
