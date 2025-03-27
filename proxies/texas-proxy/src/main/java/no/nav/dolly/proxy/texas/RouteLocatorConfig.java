package no.nav.dolly.proxy.texas;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class RouteLocatorConfig {

    @Value("${NAIS_TOKEN_ENDPOINT}")
    private String tokenEndpoint;

    @Value("${NAIS_TOKEN_EXCHANGE_ENDPOINT}")
    private String tokenExchangeEndpoint;

    @Value("${NAIS_TOKEN_INTROSPECTION_ENDPOINT}")
    private String tokenIntrospectionEndpoint;

    @PostConstruct
    void postConstruct() {
        log.info("Configured using:\nNAIS_TOKEN_ENDPOINT = {}\nNAIS_TOKEN_EXCHANGE_ENDPOINT = {}\nNAIS_TOKEN_INTROSPECTION_ENDPOINT = {}", tokenEndpoint, tokenExchangeEndpoint, tokenIntrospectionEndpoint);
    }

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(r -> r
                        .path("/api/v1/token/exchange")
                        .uri(tokenExchangeEndpoint))
                .route(r -> r
                        .path("/api/v1/introspect")
                        .uri(tokenIntrospectionEndpoint))
                .build();
    }

}
