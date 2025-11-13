package no.nav.dolly.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.web.config.Consumers;
import no.nav.dolly.web.service.AccessService;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivefrontend.filter.AddUserJwtHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.securitycore.config.UserSessionConstant;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
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
    private final Consumers consumers;
    private final GatewayFilter removeCookiesFilter = (exchange, chain) -> {
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(r -> r.headers(headers -> headers.remove(HttpHeaders.COOKIE)))
                .build();
        return chain.filter(modifiedExchange);
    };

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder
                .routes()
                .route(createRoute(consumers.getTestnavOrganisasjonFasteDataService()))
                .route(createRoute(consumers.getTestnavAdresseService()))
                .route(createRoute(consumers.getOppsummeringsdokumentService(), "oppsummeringsdokument-service"))
                .route(createRoute(consumers.getTestnavOrganisasjonForvalter()))
                .route(createRoute(consumers.getTestnavVarslingerService(), "testnav-varslinger-service"))
                .route(createRoute(consumers.getTestnavTpsMessagingService(), "testnav-tps-messaging-service"))
                .route(createRoute(consumers.getTestnorgeProfilApi(), "testnorge-profil-api"))
                .route(createRoute(consumers.getTestnavSykemeldingApi(), "testnav-sykemelding-api"))
                .route(createRoute(consumers.getTestnavBrukerService(), "testnav-bruker-service"))
                .route(createRoute(consumers.getTestnavMiljoerService()))
                .route(createRoute(consumers.getDollyBackend(), "dolly-backend"))
                .route(createRoute(consumers.getTestnavJoarkDokumentService()))
                .route(createRoute(consumers.getTestnavDollyProxy()))
                .route(createRoute(consumers.getTestnavAaregProxy()))
                .route(createRoute(consumers.getTestnavArenaForvalterenProxy()))
                .route(createRoute(consumers.getTestnavOrganisasjonService()))
                .route(createRoute(consumers.getTestnavPdlForvalter(), "testnav-pdl-forvalter"))
                .route(createRoute(consumers.getTestnavDollySearchService(), "testnav-dolly-search-service"))
                .route(createRoute(consumers.getTestnavDokarkivProxy()))
                .route(createRoute(consumers.getTestnavArbeidsplassenCVProxy()))
                .route(createRoute(consumers.getTestnavHelsepersonellService()))
                .route(createRoute(consumers.getTestnavPersonService(), "person-service"))
                .route(createRoute(consumers.getGenererNavnService()))
                .route(createRoute(consumers.getTestnavKodeverkService()))
                .route(createRoute(consumers.getTestnavTenorSearchService()))
                .route(createRoute(consumers.getTestnavSkattekortService()))
                .route(createRoute(consumers.getTestnavLevendeArbeidsforholdAnsettelse(), "testnav-levende-arbeidsforhold-ansettelse"))
                .route(createRoute(consumers.getTestnavLevendeArbeidsforholdScheduler(), "testnav-levende-arbeidsforhold-scheduler"))
                .route(createRoute(consumers.getTestnavYrkesskadeProxy()))
                .route(createRoute(consumers.getTestnavSykemeldingProxy()))
                .route(createRoute(consumers.getTestnavNomProxy()))
                .route(createRoute(consumers.getTestnavAltinn3TilgangService(), "testnav-altinn3-tilgang-service"))
                .route(createRoute(consumers.getTestnavArbeidssoekerregisteretProxy()))
                .route(createRoute(consumers.getTestnavApiOversiktService(), "testnav-oversikt-service"))
                .route(createRoute(consumers.getTestnavIdentPool()))
                .build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(DollyFrontendApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
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
                        .filters(filter, removeCookiesFilter, addUserJwtHeaderFilter())
                ).uri(host);
    }
}