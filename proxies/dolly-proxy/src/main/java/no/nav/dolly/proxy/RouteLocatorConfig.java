package no.nav.dolly.proxy;

import no.nav.dolly.proxy.altinn3.Altinn3RouteBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {

    @Bean
    RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            Altinn3RouteBuilder altinn3RouteBuilder
    ) {
        return builder
                .routes()
                .route("altinn3-route", altinn3RouteBuilder.build())
                .build();
    }

}
