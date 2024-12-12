package no.nav.testnav.altinn3tilgangproxy;

import no.nav.testnav.altinn3tilgangproxy.config.Consumers;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        InsecureJwtServerToServerConfiguration.class
})
@SpringBootApplication
public class Altinn3TilgangProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(Altinn3TilgangProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           TokenExchange tokenExchange,
                                           Consumers consumers) {

        return builder
                .routes()
                .route(spec -> spec.path("/**")
                        .filters(filterSpec -> filterSpec
                                .filter(AddAuthenticationRequestGatewayFilterFactory
                                        .bearerAuthenticationHeaderFilter(() -> tokenExchange.exchange(consumers.getAltinn3TilgangService())
                                                .map(AccessToken::getTokenValue))))
                        .uri(consumers.getAltinn3TilgangService().getUrl()))
                .build();
    }
}
