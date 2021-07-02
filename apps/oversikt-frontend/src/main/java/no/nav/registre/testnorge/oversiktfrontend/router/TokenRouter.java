package no.nav.registre.testnorge.oversiktfrontend.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TokenRouter {
    @Bean
    public RouterFunction<ServerResponse> tokenRoute(TokenHandler handler) {
        return RouterFunctions
                .route(
                        RequestPredicates.GET("/api/v1/tokens/{scope}/token/on-behalf-of"),
                        handler::onBehalfOf
                );
    }
}
