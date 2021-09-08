package no.nav.testnav.apps.oversiktfrontend;

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

import no.nav.testnav.apps.oversiktfrontend.credentials.ProfilApiServiceProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddRequestHeaderGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2FrontendConfiguration;
import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.Scopeable;
import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;

@Import({
        CoreConfig.class,
        SecureOAuth2FrontendConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class OversiktFrontendApplicationStarter {

    private final ProfilApiServiceProperties profilApiServiceProperties;
    private final TokenExchange tokenExchange;

    public static void main(String[] args) {
        SpringApplication.run(OversiktFrontendApplicationStarter.class, args);
    }

    private GatewayFilter filterFrom(ServerProperties scopeable) {
        return AddRequestHeaderGatewayFilterFactory
                .createAuthenticationHeaderFilter(
                        () -> tokenExchange
                                .generateToken(scopeable)
                                .map(AccessToken::getTokenValue)
                );
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(
                        "testnorge-profil-api",
                        profilApiServiceProperties.getUrl(),
                        filterFrom(profilApiServiceProperties)
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