package no.nav.testnav.apps.oversiktfrontend;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.credentials.PersonOrganisasjonTilgangServiceProperties;
import no.nav.testnav.apps.oversiktfrontend.credentials.ProfilApiServiceProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

import java.util.function.Function;

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

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(
                        "testnorge-profil-api",
                        profilApiServiceProperties.getUrl()
//                        addAuthenticationHeaderFilterFrom(profilApiServiceProperties)
                ))
                .route(createRoute(
                        "testnav-person-organisasjon-tilgang-service",
                        personOrganisasjonTilgangServiceProperties.getUrl()
//                        addAuthenticationHeaderFilterFrom(personOrganisasjonTilgangServiceProperties)
                ))
                .build();
    }

//    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
//        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
//                .apply(exchange -> {
//                    return tokenExchange
//                            .exchange(serverProperties, exchange)
//                            .map(AccessToken::getTokenValue);
//                });
//    }

    public static void main(String[] args) {
        SpringApplication.run(OversiktFrontendApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .setRequestHeader(HttpHeaders.AUTHORIZATION.toUpperCase(), "test")
                ).uri(host);
    }
}