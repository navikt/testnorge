package no.nav.testnav.apps.oversiktfrontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Controller
public class ForwardAndRedirectController {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html) {
        HandlerFunction<ServerResponse> indexHandler = request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html);
        return RouterFunctions
                .route(RequestPredicates.GET("/access-token/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/magic-token/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/user/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/login"), indexHandler);
    }

}
