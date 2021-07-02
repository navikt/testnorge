package no.nav.registre.testnorge.oversiktfrontend.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ApplicationRouter {
    @Bean
    public RouterFunction<ServerResponse> applicationRoute(ApplicationHandler handler) {
        return RouterFunctions
                .route(
                        RequestPredicates.GET("/api/v1/applications"),
                        handler::getApplications
                ).andRoute(
                        RequestPredicates.GET("/api/v1/applications/{name}/token/on-behalf-of"),
                        handler::onBehalfOf
                );
    }
}
