package no.nav.dolly.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.credentials.*;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivefrontend.filter.AddUserJwtHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.TestnavBrukerServiceProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.securitycore.config.UserSessionConstant;
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
        FrontendConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class DollyFrontendApplicationStarter {

    private final AccessService accessService;
    private final UserJwtExchange userJwtExchange;
    private final TestnavOrganisasjonFasteDataServiceProperties testnavOrganisasjonFasteDataServiceProperties;
    private final TestnavJoarkDokumentServiceProperties testnavJoarkDokumentServiceProperties;
    private final TestnavInntektstubProxyProperties testnavInntektstubProxyProperties;
    private final TpsMessagingServiceProperties tpsMessagingServiceProperties;
    private final TestnavBrukerServiceProperties testnavBrukerServiceProperties;
    private final TestnavBrregstubProxyProperties testnavBrregstubProxyProperties;
    private final TestnavInstProxyProperties testnavInstProxyProperties;
    private final TestnavAaregProxyProperties testnavAaregProxyProperties;
    private final TestnavArenaForvalterenProxyProperties testnavArenaForvalterenProxyProperties;
    private final TestnavKrrstubProxyProperties testnavKrrstubProxyProperties;
    private final TestnavSigrunstubProxyProperties testnavSigrunstubProxyProperties;
    private final TestnavPensjonTestdataFacadeProxyProperties testnavPensjonTestdataFacadeProxyProperties;
    private final TestnavPersonOrganisasjonTilgangServiceProperties testnavPersonOrganisasjonTilgangServiceProperties;
    private final DollyBackendProperties dollyBackendProperties;
    private final TestnorgeProfilApiProperties testnorgeProfilApiProperties;
    private final TestnavVarslingerServiceProperties testnavVarslingerServiceProperties;
    private final TestnavOrganisasjonForvalterProperties testnavOrganisasjonForvalterProperties;
    private final TestnavOrganisasjonServiceProperties testnavOrganisasjonServiceProperties;
    private final TestnavMiljoerServiceProperties testnavMiljoerServiceProperties;
    private final PersonSearchServiceProperties personSearchServiceProperties;
    private final TestnavAdresseServiceProperties testnavAdresseServiceProperties;
    private final TestnavPdlForvalterProperties testnavPdlForvalterProperties;
    private final TestnavOrganisasjonTilgangServiceProperties testnavOrganisasjonTilgangServiceProperties;
    private final TestnavNorg2ProxyProperties testnavNorg2ProxyProperties;
    private final KontoregisterProxyProperties kontoregisterProxyProperties;
    private final SkjermingsregisterProxyProperties skjermingsregisterProxyProperties;
    private final TestnavDokarkivProxyProperties testnavDokarkivProxyProperties;
    private final TestnavArbeidsplassenCvProxyProperties testnavArbeidsplassenCvProxyProperties;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(kontoregisterProxyProperties))
                .route(createRoute(testnavOrganisasjonFasteDataServiceProperties))
                .route(createRoute(testnavAdresseServiceProperties))
                .route(createRoute(testnavOrganisasjonForvalterProperties))
                .route(createRoute(testnavVarslingerServiceProperties, "testnav-varslinger-service"))
                .route(createRoute(testnorgeProfilApiProperties))
                .route(createRoute(testnavOrganisasjonTilgangServiceProperties, "testnav-organisasjon-tilgang-service"))
                .route(createRoute(tpsMessagingServiceProperties, "testnav-tps-messaging-service"))
                .route(createRoute(testnorgeProfilApiProperties, "testnorge-profil-api"))
                .route(createRoute(testnavBrukerServiceProperties, "testnav-bruker-service"))
                .route(createRoute(testnavMiljoerServiceProperties))
                .route(createRoute(dollyBackendProperties, "dolly-backend"))
                .route(createRoute(testnavJoarkDokumentServiceProperties))
                .route(createRoute(testnavPensjonTestdataFacadeProxyProperties))
                .route(createRoute(testnavInntektstubProxyProperties))
                .route(createRoute(testnavBrregstubProxyProperties))
                .route(createRoute(testnavAaregProxyProperties))
                .route(createRoute(testnavArenaForvalterenProxyProperties))
                .route(createRoute(testnavKrrstubProxyProperties, "testnav-krrstub-proxy"))
                .route(createRoute(testnavNorg2ProxyProperties, "testnav-norg2-proxy"))
                .route(createRoute(testnavInstProxyProperties, "testnav-inst-proxy"))
                .route(createRoute(testnavOrganisasjonServiceProperties))
                .route(createRoute(testnavSigrunstubProxyProperties))
                .route(createRoute(testnavPdlForvalterProperties, "testnav-pdl-forvalter"))
                .route(createRoute(personSearchServiceProperties))
                .route(createRoute(testnavPersonOrganisasjonTilgangServiceProperties, "testnav-person-organisasjon-tilgang-service"))
                .route(createRoute(skjermingsregisterProxyProperties))
                .route(createRoute(testnavDokarkivProxyProperties))
                .route(createRoute(testnavArbeidsplassenCvProxyProperties))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DollyFrontendApplicationStarter.class, args);
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
                .apply(exchange -> {
                    return accessService.getAccessToken(serverProperties, exchange);
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
