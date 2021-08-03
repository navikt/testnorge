package no.nav.testnav.apps.endringsmeldingfrontend;

import lombok.RequiredArgsConstructor;
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

import no.nav.testnav.apps.endringsmeldingfrontend.credentials.EndringsmeldingServiceProperties;
import no.nav.testnav.apps.endringsmeldingfrontend.credentials.ProfilApiServiceProperties;
import no.nav.testnav.libs.core.config.CoreConfig;
import no.nav.testnav.libs.frontend.config.FrontendConfig;
import no.nav.testnav.libs.frontend.filter.AddRequestHeaderGatewayFilterFactory;
import no.nav.testnav.libs.security.config.SecureOAuth2FrontendConfiguration;
import no.nav.testnav.libs.security.domain.AccessToken;
import no.nav.testnav.libs.security.domain.Scopeable;
import no.nav.testnav.libs.security.service.AccessTokenService;

@Import({
        CoreConfig.class,
        SecureOAuth2FrontendConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class EndringsmeldingFrontendApplicationStarter {

    private final EndringsmeldingServiceProperties endringsmeldingServiceProperties;
    private final ProfilApiServiceProperties profilApiServiceProperties;
    private final AccessTokenService accessTokenService;

    public static void main(String[] args) {
        SpringApplication.run(EndringsmeldingFrontendApplicationStarter.class, args);
    }

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
                        "endringsmelding-service",
                        endringsmeldingServiceProperties.getUrl(),
                        filterFrom(endringsmeldingServiceProperties)
                ))
                .route(createRoute(
                        "testnorge-profil-api",
                        profilApiServiceProperties.getUrl(),
                        filterFrom(profilApiServiceProperties)
                ))
                .build();
    }

    private Function<PredicateSpec, Route.AsyncBuilder> createRoute(String segment, String host, GatewayFilter filter) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }

}
