package no.nav.testnav.proxies.samhandlerregisteretproxy;

import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.samhandlerregisteretproxy.config.credentials.SamhandlerregisteretProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class SamhandlerregisteretProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(SamhandlerregisteretProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, TrygdeetatenAzureAdTokenService tokenService, SamhandlerregisteretProperties properties) {

        var addAuthenticationHeaderDevFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(properties).map(AccessToken::getTokenValue));

        return builder.routes()
                .route(spec -> spec.path("/**")
                        .filters(filterSpec -> filterSpec.filter(addAuthenticationHeaderDevFilter))
                        .uri(properties.getUrl()))
                .build();
    }
}