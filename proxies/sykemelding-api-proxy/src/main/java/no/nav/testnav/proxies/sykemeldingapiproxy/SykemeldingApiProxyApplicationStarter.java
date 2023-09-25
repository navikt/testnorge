package no.nav.testnav.proxies.sykemeldingapiproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.NavAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.sykemeldingapiproxy.config.SykemeldingApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class SykemeldingApiProxyApplicationStarter {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, NavAzureAdTokenService tokenService, SykemeldingApiProperties properties) {
        var addAuthenticationHeaderDevFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(properties).map(AccessToken::getTokenValue));

        return builder.routes()
                .route(spec -> spec
                        .path("/**")
                        .filters(filterSpec -> filterSpec.filter(addAuthenticationHeaderDevFilter))
                        .uri(properties.getUrl())
                )
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SykemeldingApiProxyApplicationStarter.class, args);
    }
}