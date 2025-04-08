package no.nav.dolly.proxy.texas.local;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
@Profile("local")
public class OpenApiConfig {

    @Bean
    RouterFunction<ServerResponse> routeToSwagger() {
        return RouterFunctions
                .route()
                .GET("/swagger", request -> ServerResponse
                        .temporaryRedirect(URI.create("/webjars/swagger-ui/index.html"))
                        .build())
                .build();
    }

}
