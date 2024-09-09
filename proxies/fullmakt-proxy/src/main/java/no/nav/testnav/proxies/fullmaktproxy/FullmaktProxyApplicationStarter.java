package no.nav.testnav.proxies.fullmaktproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.fullmaktproxy.config.Consumers;
import no.nav.testnav.proxies.fullmaktproxy.config.LocalVaultConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        LocalVaultConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class FullmaktProxyApplicationStarter {

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            TrygdeetatenAzureAdTokenService tokenService,
            Consumers consumers
    ) {
        var addAuthenticationHeaderDevFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenService
                                .exchange(consumers.getFullmakt())
                                .map(AccessToken::getTokenValue));
        return builder
                .routes()
                .route(
                        spec -> spec.path("/api/v2/**")
                                .filters(filterSpec -> filterSpec.filter(addAuthenticationHeaderDevFilter))
                                .uri(consumers.getFullmakt().getUrl()))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FullmaktProxyApplicationStarter.class, args);
    }
}
