package no.nav.dolly.proxy.route;

import io.swagger.v3.core.filter.SpecFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RouteLocatorConfig {

    private final Altinn3RouteBuilder altinn3RouteBuilder;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder
                .routes()

                .route(altinn3RouteBuilder.buildApiRoute())
                .route(altinn3RouteBuilder.buildOpenApiRoute())

                .build();

    }

}