package no.nav.testnav.proxies.yrkesskadeproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.yrkesskadeproxy.config.Consumers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class YrkesskadeProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(YrkesskadeProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           GatewayFilter authenticationFilter,
                                           Consumers consumers) {

        return builder
                .routes()
                .route(spec -> spec
                        .path("/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(consumers.getYrkesskade().getUrl()))
                .build();
    }

    @Bean
    GatewayFilter getAuthenticationFilter(
            AzureAdTokenService tokenService,
            Consumers consumers) {

        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService
                        .exchange(consumers.getYrkesskade())
                        .map(AccessToken::getTokenValue));
    }
}