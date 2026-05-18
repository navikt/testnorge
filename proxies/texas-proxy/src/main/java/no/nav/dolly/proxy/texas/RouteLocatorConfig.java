package no.nav.dolly.proxy.texas;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local")
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
        log.info("Configured using:\n\tNAIS_TOKEN_ENDPOINT = {}\n\tNAIS_TOKEN_EXCHANGE_ENDPOINT = {}\n\tNAIS_TOKEN_INTROSPECTION_ENDPOINT = {}", tokenEndpoint, tokenExchangeEndpoint, tokenIntrospectionEndpoint);
    }

    @Bean("texasRouteLocator")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(r -> r
                        .path("/api/v1/token")
                        .uri(tokenEndpoint))
                .route(r -> r
                        .path("/api/v1/token/exchange")
                        .uri(tokenExchangeEndpoint))
                .route(r -> r
                        .path("/api/v1/introspect")
                        .uri(tokenIntrospectionEndpoint))
                .build();
    }

}
