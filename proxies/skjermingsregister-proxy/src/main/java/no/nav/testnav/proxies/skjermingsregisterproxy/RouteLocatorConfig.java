package no.nav.testnav.proxies.skjermingsregisterproxy;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            SkjermingsregisterProperties serverProperties,
            GatewayFilter authenticationFilter
    ) {
        return builder
                .routes()
                .route(spec -> spec
                        .path("/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(serverProperties.getUrl()))
                .build();
    }

}
