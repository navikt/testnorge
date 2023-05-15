package no.nav.testnav.proxies.medlproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.medlproxy.config.LocalVaultConfig;
import no.nav.testnav.proxies.medlproxy.config.credentials.MedlStubProperties;
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
public class MedlProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(MedlProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, TrygdeetatenAzureAdTokenService tokenService, MedlStubProperties properties) {

        var addAuthenticationHeaderDevFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(properties).map(AccessToken::getTokenValue));

        return builder.routes()
                .route(spec -> spec.path("/api/v2/**")
                        .filters(filterSpec -> filterSpec.filter(addAuthenticationHeaderDevFilter))
                        .uri(properties.getUrl()))
                .build();
    }
}
