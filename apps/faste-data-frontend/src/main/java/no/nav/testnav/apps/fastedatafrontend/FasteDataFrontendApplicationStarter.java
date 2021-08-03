package no.nav.testnav.apps.fastedatafrontend;

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

import no.nav.testnav.apps.fastedatafrontend.credentials.OrganisasjonFasteDataServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.OrganisasjonServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.PersonFasteDataServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.PersonServiceProperties;
import no.nav.testnav.apps.fastedatafrontend.credentials.ProfilApiServiceProperties;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.frontend.config.FrontendConfig;
import no.nav.testnav.libs.frontend.filter.AddRequestHeaderGatewayFilterFactory;
import no.nav.testnav.libs.security.config.SecureOAuth2FrontendConfiguration;
import no.nav.testnav.libs.security.domain.AccessToken;
import no.nav.testnav.libs.security.domain.Scopeable;
import no.nav.testnav.libs.security.service.AccessTokenService;

@Slf4j
@Import({
        CoreConfig.class,
        SecureOAuth2FrontendConfiguration.class,
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

    public static void main(String[] args) {
        SpringApplication.run(FasteDataFrontendApplicationStarter.class, args);
    }

    private final AccessTokenService accessTokenService;


    private GatewayFilter filterFrom(Scopeable scopeable) {
        return AddRequestHeaderGatewayFilterFactory
                .createAuthenticationHeaderFilter(
                        () -> accessTokenService
                                .generateToken(scopeable)
                                .map(AccessToken::getTokenValue)
                );
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(
                        "testnav-organisasjon-service",
                        organisasjonServiceProperties.getUrl(),
                        filterFrom(organisasjonServiceProperties)
                ))
                .route(createRoute(
                        "testnav-organisasjon-faste-data-service",
                        organisasjonFasteDataServiceProperties.getUrl(),
                        filterFrom(organisasjonFasteDataServiceProperties)
                ))
                .route(createRoute(
                        "testnorge-profil-api",
                        profilApiServiceProperties.getUrl(),
                        filterFrom(profilApiServiceProperties)
                ))
                .route(createRoute(
                        "testnav-person-service",
                        personServiceProperties.getUrl(),
                        filterFrom(personServiceProperties)
                ))
                .route(createRoute(
                        "testnav-person-faste-data-service",
                        personFasteDataServiceProperties.getUrl(),
                        filterFrom(personFasteDataServiceProperties)
                ))
                .build();
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
