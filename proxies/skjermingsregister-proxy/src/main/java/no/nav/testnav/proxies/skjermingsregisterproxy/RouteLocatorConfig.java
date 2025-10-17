package no.nav.testnav.proxies.skjermingsregisterproxy;

import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
        SecureOAuth2ServerToServerConfiguration.class,
        SecurityConfig.class
})
@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            Consumers consumers,
            GatewayFilter authenticationFilter
    ) {
        return builder
                .routes()
                .route(spec -> spec
                        .path("/**")
                        .and()
                        .not(not -> not.path("/internal/**"))
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(consumers.getSkjermingsregister().getUrl()))
                .build();
    }

    @Bean
    GatewayFilter getAuthenticationFilter(
            AzureTrygdeetatenTokenService tokenService,
            Consumers consumers
    ) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService
                        .exchange(consumers.getSkjermingsregister())
                        .map(AccessToken::getTokenValue));
    }

}
