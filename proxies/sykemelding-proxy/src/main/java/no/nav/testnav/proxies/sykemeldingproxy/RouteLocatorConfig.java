package no.nav.testnav.proxies.sykemeldingproxy;

import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
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
            AzureTrygdeetatenTokenService tokenService,
            Consumers consumers) {

        return builder
                .routes()
                .route(spec -> spec
                        .path("/syfosmregler/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(authFilter(tokenService, consumers.getSyfosmregler()))
                        )
                        .uri(consumers.getSyfosmregler().getUrl()))
                .route(spec -> spec
                        .path("/tsm/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(authFilter(tokenService, consumers.getTsmInputDolly()))
                        )
                        .uri(consumers.getTsmInputDolly().getUrl()))
                .build();
    }

    private GatewayFilter authFilter(AzureTrygdeetatenTokenService tokenService, ServerProperties serverProperties) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() ->
                        tokenService.exchange(serverProperties).map(AccessToken::getTokenValue));
    }
}
