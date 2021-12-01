package no.nav.testnav.apps.fastedatafrontend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import no.nav.testnav.apps.fastedatafrontend.credentials.OrganisasjonFasteDataServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.OrganisasjonServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.PersonFasteDataServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.PersonServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.ProfilApiServiceProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Slf4j
@Import({
        CoreConfig.class,
        OicdInMemorySessionConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class FasteDataFrontendApplicationStarter {

    private final ProfilApiServiceProperties profilApiServiceProperties;
    private final OrganisasjonServiceProperties organisasjonServiceProperties;
    private final OrganisasjonFasteDataServiceProperties organisasjonFasteDataServiceProperties;
    private final PersonServiceProperties personServiceProperties;
    private final PersonFasteDataServiceProperties personFasteDataServiceProperties;
    private final TokenExchange tokenExchange;

    public static void main(String[] args) {
        SpringApplication.run(FasteDataFrontendApplicationStarter.class, args);
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
                        "testnav-organisasjon-service",
                        organisasjonServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(organisasjonServiceProperties)
                ))
                .route(createRoute(
                        "testnav-organisasjon-faste-data-service",
                        organisasjonFasteDataServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(organisasjonFasteDataServiceProperties)
                ))
                .route(createRoute(
                        "testnorge-profil-api",
                        profilApiServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(profilApiServiceProperties)
                ))
                .route(createRoute(
                        "testnav-person-service",
                        personServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(personServiceProperties)
                ))
                .route(createRoute(
                        "testnav-person-faste-data-service",
                        personFasteDataServiceProperties.getUrl(),
                        addAuthenticationHeaderFilterFrom(personFasteDataServiceProperties)
                ))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host, GatewayFilter filter) {
        log.info("Redirect fra segment {} til host {}.", segment, host);
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }
}
