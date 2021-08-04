package no.nav.testnav.libs.reactivecore.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class InternalRouter {

    @Bean
    public RouterFunction<ServerResponse> internalRoute(InternalHandler internalHandler) {
        return RouterFunctions
                .route(
                        RequestPredicates.GET("/internal/isAlive").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                        internalHandler::isAlive
                ).andRoute(
                        RequestPredicates.GET("/internal/isReady").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                        internalHandler::isReady
                );
    }


}
