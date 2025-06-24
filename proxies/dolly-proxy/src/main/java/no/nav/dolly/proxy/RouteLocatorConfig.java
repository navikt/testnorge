package no.nav.dolly.proxy;

import io.swagger.v3.core.filter.SpecFilter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.altinn3.Altinn3RouteBuilder;
import no.nav.dolly.proxy.brregstub.BrregStubRouteBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteLocatorConfig {

    private final Altinn3RouteBuilder altinn3RouteBuilder;
    private final BrregStubRouteBuilder brregStubRouteBuilder;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, SpecFilter specFilter) {

        return builder
                .routes()
                //.route(altinn3RouteBuilder.buildOpenApiRoute()) Commented out for now.
                .route(altinn3RouteBuilder.buildApiRoute())
                .route(brregStubRouteBuilder.buildApiRoute())
                .build();

    }

}