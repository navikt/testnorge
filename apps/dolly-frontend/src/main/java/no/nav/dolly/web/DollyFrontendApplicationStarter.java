package no.nav.dolly.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.credentials.DollyBackendProperties;
import no.nav.dolly.web.credentials.PersonSearchServiceProperties;
import no.nav.dolly.web.credentials.TestnavAdresseServiceProperties;
import no.nav.dolly.web.credentials.TestnavArenaForvalterenProxyProperties;
import no.nav.dolly.web.credentials.TestnavBrukerServiceProperties;
import no.nav.dolly.web.credentials.TestnavBrregstubProxyProperties;
import no.nav.dolly.web.credentials.TestnavHodejegerenProxyProperties;
import no.nav.dolly.web.credentials.TestnavInntektstubProxyProperties;
import no.nav.dolly.web.credentials.TestnavJoarkDokumentServiceProperties;
import no.nav.dolly.web.credentials.TestnavKrrstubProxyProperties;
import no.nav.dolly.web.credentials.TestnavMiljoerServiceProperties;
import no.nav.dolly.web.credentials.TestnavOrganisasjonFasteDataServiceProperties;
import no.nav.dolly.web.credentials.TestnavOrganisasjonForvalterProperties;
import no.nav.dolly.web.credentials.TestnavOrganisasjonServiceProperties;
import no.nav.dolly.web.credentials.TestnavPensjonTestdataFacadeProxyProperties;
import no.nav.dolly.web.credentials.TestnavPersonOrganisasjonTilgangServiceProperties;
import no.nav.dolly.web.credentials.TestnavSigrunstubProxyProperties;
import no.nav.dolly.web.credentials.TestnavTestnorgeAaregProxyProperties;
import no.nav.dolly.web.credentials.TestnavTestnorgeInstProxyProperties;
import no.nav.dolly.web.credentials.TestnavVarslingerServiceProperties;
import no.nav.dolly.web.credentials.TestnorgeProfilApiProperties;
import no.nav.dolly.web.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.web.credentials.UdiStubProxyProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivefrontend.filter.AddUserJwtHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.securitycore.UserSessionConstant;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

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

@Slf4j
@Import({
        CoreConfig.class,
        FrontendConfig.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class DollyFrontendApplicationStarter {

    private final TokenExchange tokenExchange;
    private final UserJwtExchange userJwtExchange;

    private final TestnavOrganisasjonFasteDataServiceProperties testnavOrganisasjonFasteDataServiceProperties;
    private final TestnavJoarkDokumentServiceProperties testnavJoarkDokumentServiceProperties;
    private final TestnavInntektstubProxyProperties testnavInntektstubProxyProperties;
    private final TpsForvalterenProxyProperties tpsForvalterenProxyProperties;
    private final TestnavBrukerServiceProperties testnavBrukerServiceProperties;
    private final TestnavBrregstubProxyProperties testnavBrregstubProxyProperties;
    private final TestnavHodejegerenProxyProperties testnavHodejegerenProxyProperties;
    private final TestnavArenaForvalterenProxyProperties testnavArenaForvalterenProxyProperties;
    private final TestnavTestnorgeAaregProxyProperties testnavTestnorgeAaregProxyProperties;
    private final TestnavKrrstubProxyProperties testnavKrrstubProxyProperties;
    private final TestnavTestnorgeInstProxyProperties testnavTestnorgeInstProxyProperties;
    private final TestnavSigrunstubProxyProperties testnavSigrunstubProxyProperties;
    private final TestnavPensjonTestdataFacadeProxyProperties testnavPensjonTestdataFacadeProxyProperties;
    private final TestnavPersonOrganisasjonTilgangServiceProperties testnavPersonOrganisasjonTilgangServiceProperties;
    private final DollyBackendProperties dollyBackendProperties;
    private final TestnorgeProfilApiProperties testnorgeProfilApiProperties;
    private final TestnavVarslingerServiceProperties testnavVarslingerServiceProperties;
    private final TestnavOrganisasjonForvalterProperties testnavOrganisasjonForvalterProperties;
    private final TestnavOrganisasjonServiceProperties testnavOrganisasjonServiceProperties;
    private final TestnavMiljoerServiceProperties testnavMiljoerServiceProperties;
    private final UdiStubProxyProperties udiStubProxyProperties;
    private final PersonSearchServiceProperties personSearchServiceProperties;
    private final TestnavAdresseServiceProperties testnavAdresseServiceProperties;

    public static void main(String[] args) {
        SpringApplication.run(DollyFrontendApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(testnavOrganisasjonFasteDataServiceProperties))
                .route(createRoute(testnavAdresseServiceProperties))
                .route(createRoute(testnavOrganisasjonForvalterProperties))
                .route(createRoute(testnavVarslingerServiceProperties, "testnav-varslinger-service"))
                .route(createRoute(testnorgeProfilApiProperties))
                .route(createRoute(testnavMiljoerServiceProperties))
                .route(createRoute(dollyBackendProperties, "dolly-backend"))
                .route(createRoute(testnavJoarkDokumentServiceProperties))
                .route(createRoute(testnavPensjonTestdataFacadeProxyProperties))
                .route(createRoute(testnavInntektstubProxyProperties))
                .route(createRoute(testnavHodejegerenProxyProperties))
                .route(createRoute(tpsForvalterenProxyProperties, "tps-forvalteren-proxy"))
                .route(createRoute(testnavBrregstubProxyProperties))
                .route(createRoute(testnavArenaForvalterenProxyProperties))
                .route(createRoute(testnavTestnorgeInstProxyProperties))
                .route(createRoute(testnavTestnorgeAaregProxyProperties))
                .route(createRoute(testnavKrrstubProxyProperties, "testnav-krrstub-proxy"))
                .route(createRoute(testnavOrganisasjonServiceProperties))
                .route(createRoute(testnavSigrunstubProxyProperties))
                .route(createRoute(udiStubProxyProperties, "udi-stub"))
                .route(createRoute(personSearchServiceProperties))
                .route(createRoute(testnavPersonOrganisasjonTilgangServiceProperties, "testnav-person-organisasjon-tilgang-service"))
                .route(createRoute(testnavBrukerServiceProperties, "testnav-bruker-service"))
                .build();
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
                .apply(exchange -> {
                    return tokenExchange
                            .generateToken(serverProperties, exchange)
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

    private Function<PredicateSpec, Buildable<Route>> createRoute(ServerProperties serverProperties) {
        return createRoute(
                serverProperties.getName(),
                serverProperties.getUrl(),
                addAuthenticationHeaderFilterFrom(serverProperties)
        );
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(ServerProperties serverProperties, String segment) {
        return createRoute(
                segment,
                serverProperties.getUrl(),
                addAuthenticationHeaderFilterFrom(serverProperties)
        );
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
