package no.nav.dolly.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.function.Function;

import no.nav.dolly.web.credentials.DollyBackendProperties;
import no.nav.dolly.web.credentials.PersonSearchServiceProperties;
import no.nav.dolly.web.credentials.TestnavAdresseServiceProperties;
import no.nav.dolly.web.credentials.TestnavArenaForvalterenProxyProperties;
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
import no.nav.dolly.web.credentials.TestnavSigrunstubProxyProperties;
import no.nav.dolly.web.credentials.TestnavTestnorgeAaregProxyProperties;
import no.nav.dolly.web.credentials.TestnavTestnorgeInstProxyProperties;
import no.nav.dolly.web.credentials.TestnavVarslingerApiProperties;
import no.nav.dolly.web.credentials.TestnorgeProfilApiProperties;
import no.nav.dolly.web.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.web.credentials.UdiStubProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddRequestHeaderGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2FrontendConfiguration;
import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;
import no.nav.testnav.libs.reactivesecurity.domain.Scopeable;
import no.nav.testnav.libs.reactivesecurity.service.AccessTokenService;

@Slf4j
@Import({
        CoreConfig.class,
        SecureOAuth2FrontendConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class ApplicationStarter {

    private final AccessTokenService accessTokenService;

    private final TestnavOrganisasjonFasteDataServiceProperties testnavOrganisasjonFasteDataServiceProperties;
    private final TestnavJoarkDokumentServiceProperties testnavJoarkDokumentServiceProperties;
    private final TestnavInntektstubProxyProperties testnavInntektstubProxyProperties;
    private final TpsForvalterenProxyProperties tpsForvalterenProxyProperties;
    private final TestnavBrregstubProxyProperties testnavBrregstubProxyProperties;
    private final TestnavHodejegerenProxyProperties testnavHodejegerenProxyProperties;
    private final TestnavArenaForvalterenProxyProperties testnavArenaForvalterenProxyProperties;
    private final TestnavTestnorgeAaregProxyProperties testnavTestnorgeAaregProxyProperties;
    private final TestnavKrrstubProxyProperties testnavKrrstubProxyProperties;
    private final TestnavTestnorgeInstProxyProperties testnavTestnorgeInstProxyProperties;
    private final TestnavSigrunstubProxyProperties testnavSigrunstubProxyProperties;
    private final TestnavPensjonTestdataFacadeProxyProperties testnavPensjonTestdataFacadeProxyProperties;
    private final DollyBackendProperties dollyBackendProperties;
    private final TestnorgeProfilApiProperties testnorgeProfilApiProperties;
    private final TestnavVarslingerApiProperties testnorgeVarslingerApiProperties;
    private final TestnavOrganisasjonForvalterProperties testnavOrganisasjonForvalterProperties;
    private final TestnavOrganisasjonServiceProperties testnavOrganisasjonServiceProperties;
    private final TestnavMiljoerServiceProperties testnavMiljoerServiceProperties;
    private final UdiStubProperties udiStubProperties;
    private final PersonSearchServiceProperties personSearchServiceProperties;
    private final TestnavAdresseServiceProperties testnavAdresseServiceProperties;

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(testnavOrganisasjonFasteDataServiceProperties))
                .route(createRoute(testnavAdresseServiceProperties))
                .route(createRoute(testnavOrganisasjonForvalterProperties))
                .route(createRoute(testnorgeVarslingerApiProperties))
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
                .route(createRoute(testnavKrrstubProxyProperties))
                .route(createRoute(testnavOrganisasjonServiceProperties))
                .route(createRoute(testnavSigrunstubProxyProperties))
                .route(createRoute(udiStubProperties, "udi-stub"))
                .route(createRoute(personSearchServiceProperties))
                .build();
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(Scopeable scopeable) {
        return AddRequestHeaderGatewayFilterFactory
                .createAuthenticationHeaderFilter(
                        () -> accessTokenService
                                .generateToken(scopeable)
                                .map(AccessToken::getTokenValue)
                );
    }

    private Function<PredicateSpec, Route.AsyncBuilder> createRoute(NaisServerProperties naisServerProperties) {
        return createRoute(
                naisServerProperties.getName(),
                naisServerProperties.getUrl(),
                addAuthenticationHeaderFilterFrom(naisServerProperties)
        );
    }

    private Function<PredicateSpec, Route.AsyncBuilder> createRoute(NaisServerProperties naisServerProperties, String segment) {
        return createRoute(
                segment,
                naisServerProperties.getUrl(),
                addAuthenticationHeaderFilterFrom(naisServerProperties)
        );
    }

    private Function<PredicateSpec, Route.AsyncBuilder> createRoute(String segment, String host, GatewayFilter filter) {
        log.info("Redirect fra segment {} til host {}.", segment, host);
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }

}
