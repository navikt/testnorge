package no.nav.dolly.proxy;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.altinn3.Altinn3RouteBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteLocatorConfig {

    private final Altinn3RouteBuilder altinn3RouteBuilder;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder
                .routes()
                .route(altinn3RouteBuilder.buildOpenApiRoute())
                .route(altinn3RouteBuilder.buildApiRoute())
                .build();

    }
}