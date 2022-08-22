package no.nav.testnav.apps.organisasjontilgangfrontend.router;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class LoginRouter {

    @Bean
    public RouterFunction<ServerResponse> loginRoute(LoginHandler loginHandler) {
        return RouterFunctions
                .route(
                        RequestPredicates.GET("/login").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                        loginHandler::forward
                );
    }

}
