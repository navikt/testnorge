package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RouteLocatorConfig {

    private final Inntektstub inntektstub;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder
                .routes()
                .route("inntektstub-proxy", inntektstub.build())
                .build();

    }

}