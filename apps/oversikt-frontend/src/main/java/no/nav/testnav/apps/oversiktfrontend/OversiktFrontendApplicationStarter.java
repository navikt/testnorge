package no.nav.testnav.apps.oversiktfrontend;

import lombok.RequiredArgsConstructor;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;
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
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

import no.nav.testnav.apps.oversiktfrontend.credentials.PersonOrganisasjonTilgangServiceProperties;
import no.nav.testnav.apps.oversiktfrontend.credentials.ProfilApiServiceProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivefrontend.filter.AddUserJwtHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.TestnavBrukerServiceProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.securitycore.config.UserSessionConstant;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Import({
        CoreConfig.class,
        OicdInMemorySessionConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class OversiktFrontendApplicationStarter {

    private final ProfilApiServiceProperties profilApiServiceProperties;
    private final PersonOrganisasjonTilgangServiceProperties personOrganisasjonTilgangServiceProperties;
    private final TestnavBrukerServiceProperties testnavBrukerServiceServiceProperties;
    private final TokenExchange tokenExchange;
    private final UserJwtExchange userJwtExchange;

    public static void main(String[] args) {
        SpringApplication.run(OversiktFrontendApplicationStarter.class, args);
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
                .apply(exchange -> {
                    return tokenExchange
                            .exchange(serverProperties, exchange)
                            .map(AccessToken::getTokenValue);
                });
    }

    private GatewayFilter addUserJwtHeaderFilter() {
        return new AddUserJwtHeaderToRequestGatewayFilterFactory().apply(exchange -> {
            return exchange.getSession()
                    .flatMap(session -> Optional.ofNullable(session.getAttribute(UserSessionConstant.SESSION_USER_ID_KEY))
                            .map(value -> Mono.just((String) value))
                            .orElse(Mono.empty())
                    ).flatMap(id -> userJwtExchange.generateJwt(id, exchange));
        });
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(
                        "testnorge-profil-api",
                        profilApiServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(profilApiServiceProperties)
                ))
                .route(createRoute(
                        "testnav-person-organisasjon-tilgang-service",
                        personOrganisasjonTilgangServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(personOrganisasjonTilgangServiceProperties)
                ))
                .route(createRoute(
                        "testnav-bruker-service",
                        testnavBrukerServiceServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(testnavBrukerServiceServiceProperties)
                ))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host, GatewayFilter filter) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filters(filter, addUserJwtHeaderFilter())
                ).uri(host);
    }
}