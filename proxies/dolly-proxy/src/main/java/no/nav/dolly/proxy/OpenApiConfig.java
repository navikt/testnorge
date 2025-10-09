package no.nav.dolly.proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
class OpenApiConfig {

    @Bean
    RouterFunction<ServerResponse> swaggerRedirect() {
        return RouterFunctions
                .route()
                .GET("/swagger", request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build())
                .build();
    }

}
