package no.nav.testnav.apps.endringsmeldingfrontend;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.function.Function;

import no.nav.testnav.apps.endringsmeldingfrontend.credentials.EndringsmeldingServiceProperties;
import no.nav.testnav.apps.endringsmeldingfrontend.credentials.ProfilApiServiceProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Import({
        CoreConfig.class,
        OicdInMemorySessionConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class EndringsmeldingFrontendApplicationStarter {

    private final EndringsmeldingServiceProperties endringsmeldingServiceProperties;
    private final ProfilApiServiceProperties profilApiServiceProperties;
    private final TokenExchange tokenExchange;

    public static void main(String[] args) {
        SpringApplication.run(EndringsmeldingFrontendApplicationStarter.class, args);
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
                .apply(exchange -> {
                    return tokenExchange
                            .generateToken(serverProperties, exchange)
                            .map(AccessToken::getTokenValue);
                });
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(
                        "endringsmelding-service",
                        endringsmeldingServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(endringsmeldingServiceProperties)
                ))
                .route(createRoute(
                        "testnorge-profil-api",
                        profilApiServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(profilApiServiceProperties)
                ))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host, GatewayFilter filter) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }
}
