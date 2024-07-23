package no.nav.dolly.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final Consumers consumers;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(consumers.getTestnavKontoregisterPersonProxy()))
                .route(createRoute(consumers.getTestnavOrganisasjonFasteDataService()))
                .route(createRoute(consumers.getTestnavAdresseService()))
                .route(createRoute(consumers.getOppsummeringsdokumentService(), "oppsummeringsdokument-service"))
                .route(createRoute(consumers.getTestnavOrganisasjonForvalter()))
                .route(createRoute(consumers.getTestnavVarslingerService(), "testnav-varslinger-service"))
                .route(createRoute(consumers.getTestnorgeProfilApi())) // Duplicate, see below.
                .route(createRoute(consumers.getTestnavOrganisasjonTilgangService(), "testnav-organisasjon-tilgang-service"))
                .route(createRoute(consumers.getTestnavTpsMessagingService(), "testnav-tps-messaging-service"))
                .route(createRoute(consumers.getTestnorgeProfilApi(), "testnorge-profil-api")) // Duplicate, see above.
                .route(createRoute(consumers.getTestnavBrukerService(), "testnav-bruker-service"))
                .route(createRoute(consumers.getTestnavMiljoerService()))
                .route(createRoute(consumers.getDollyBackend(), "dolly-backend"))
                .route(createRoute(consumers.getTestnavJoarkDokumentService()))
                .route(createRoute(consumers.getTestnavPensjonTestdataFacadeProxy()))
                .route(createRoute(consumers.getTestnavInntektstubProxy()))
                .route(createRoute(consumers.getTestnavBrregstubProxy()))
                .route(createRoute(consumers.getTestnavAaregProxy()))
                .route(createRoute(consumers.getTestnavUdistubProxy(), "testnav-udistub-proxy"))
                .route(createRoute(consumers.getTestnavArenaForvalterenProxy()))
                .route(createRoute(consumers.getTestnavKrrstubProxy(), "testnav-krrstub-proxy"))
                .route(createRoute(consumers.getTestnavMedlProxy(), "testnav-medl-proxy"))
                .route(createRoute(consumers.getTestnavNorg2Proxy(), "testnav-norg2-proxy"))
                .route(createRoute(consumers.getTestnavInstProxy(), "testnav-inst-proxy"))
                .route(createRoute(consumers.getTestnavHistarkProxy(), "testnav-histark-proxy"))
                .route(createRoute(consumers.getTestnavOrganisasjonService()))
                .route(createRoute(consumers.getTestnavSigrunstubProxy()))
                .route(createRoute(consumers.getTestnavPdlForvalter(), "testnav-pdl-forvalter"))
                .route(createRoute(consumers.getTestnavPersonSearchService()))
                .route(createRoute(consumers.getTestnavPersonOrganisasjonTilgangService(), "testnav-person-organisasjon-tilgang-service"))
                .route(createRoute(consumers.getTestnavSkjermingsregisterProxy()))
                .route(createRoute(consumers.getTestnavDokarkivProxy()))
                .route(createRoute(consumers.getTestnavArbeidsplassenCVProxy()))
                .route(createRoute(consumers.getTestnavHelsepersonellService()))
                .route(createRoute(consumers.getTestnavPersonService(), "person-service"))
                .route(createRoute(consumers.getGenererNavnService()))
                .route(createRoute(consumers.getTestnavKodeverkService()))
                .route(createRoute(consumers.getTestnavTenorSearchService()))
                .route(createRoute(consumers.getTestnavLevendeArbeidsforholdAnsettelsev2(), "testnav-levende-arbeidsforhold-ansettelsev2"))
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
