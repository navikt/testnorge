package no.nav.testnav.apps.organisasjontilgangfrontend;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.organisasjontilgangfrontend.config.Consumers;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesessionsecurity.config.OidcInMemorySessionConfiguration;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.function.Function;

@Import({
        CoreConfig.class,
        OidcInMemorySessionConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication(exclude = { ReactiveUserDetailsServiceAutoConfiguration.class })
@RequiredArgsConstructor
public class OrganisasjonTilgangFrontendApplicationStarter {

    private final Consumers consumers;
    private final TokenExchange tokenExchange;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(consumers.getTestnavOrganisasjonTilgangService()))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(OrganisasjonTilgangFrontendApplicationStarter.class, args);
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
                .apply(exchange -> {
                    return tokenExchange
                            .exchange(serverProperties, exchange)
                            .map(AccessToken::getTokenValue);
                });
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(ServerProperties serverProperties) {
        var host = serverProperties.getUrl();
        var filter = addAuthenticationHeaderFilterFrom(serverProperties);
        return spec -> spec
                .path("/organisasjon-tilgang-service/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + "organisasjon-tilgang-service" + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }
}