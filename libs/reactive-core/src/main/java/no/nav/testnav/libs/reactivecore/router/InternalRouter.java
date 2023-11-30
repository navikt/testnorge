package no.nav.testnav.libs.reactivecore.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@Configuration
public class InternalRouter {

    @Bean
    public RouterFunction<ServerResponse> internalRoute(InternalHandler internalHandler) {
        return RouterFunctions
                .route(
                        RequestPredicates.GET("/internal/isAlive").and(RequestPredicates.accept(TEXT_PLAIN)),
                        internalHandler::isAlive
                ).andRoute(
                        RequestPredicates.GET("/internal/isReady").and(RequestPredicates.accept(TEXT_PLAIN)),
                        internalHandler::isReady
                ).andRoute(
                        RequestPredicates.GET("/internal/image").and(RequestPredicates.accept(TEXT_PLAIN, APPLICATION_JSON)),
                        internalHandler::getVersion
                );
    }
}
