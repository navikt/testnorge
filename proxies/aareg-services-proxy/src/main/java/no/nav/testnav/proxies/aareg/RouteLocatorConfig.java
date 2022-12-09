package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;

import java.util.function.Function;
import java.util.stream.Stream;

@Import({
        SecureOAuth2ServerToServerConfiguration.class,
        SecurityConfig.class
})
@Configuration
public class RouteLocatorConfig {

    // TODO: I følge https://github.com/navikt/aareg-services er kun Q0, Q1 og Q2 (med annet URL-pattern) tilgjengelig. Ser at dolly-backend opererer med miljøene under. Sjekk hvilke vi ønsker.
    private static final String[] ENV_PREPROD = {"q1", "q2", "q4", "q5"};
    private static final String[] ENV_TEST = {"t0", "t1", "t3", "t4", "t5"};

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            @Qualifier("preprod") StsOidcTokenService preprodStsOidcTokenService,
            @Qualifier("test") StsOidcTokenService testStsOidcTokenService,
            TrygdeetatenAzureAdTokenService tokenService,
            AzureConfig azureProperties
    ) {

        var preprodStsAuthentication = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(preprodStsOidcTokenService::getToken);
        var testStsAuthentication = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(testStsOidcTokenService::getToken);

        var routes = builder.routes();

        Stream.of(ENV_PREPROD)
                .forEach(env -> {
                    var azureAuthentication = AddAuthenticationRequestGatewayFilterFactory
                            .bearerAuthenticationHeaderFilter(() -> tokenService
                                    .exchange(azureProperties.forEnvironment(env))
                                    .map(AccessToken::getTokenValue));
                    routes
                            .route(createReadableRouteToNewEndpoint(env, azureAuthentication))
                            .route(createWritableRouteToOldEndpoint(env, preprodStsAuthentication));
                });
        Stream.of(ENV_TEST)
                .forEach(env -> {
                    var azureAuthentication = AddAuthenticationRequestGatewayFilterFactory
                            .bearerAuthenticationHeaderFilter(() -> tokenService
                                    .exchange(azureProperties.forEnvironment(env))
                                    .map(AccessToken::getTokenValue));
                    routes
                            .route(createReadableRouteToNewEndpoint(env, azureAuthentication))
                            .route(createWritableRouteToOldEndpoint(env, testStsAuthentication));
                });

        return routes.build();

    }

    private Function<PredicateSpec, Buildable<Route>> createWritableRouteToOldEndpoint(String env, GatewayFilter authentication) {
        return spec -> spec
                .path("/" + env + "/**")
                .and().not(p -> p
                        .path("/.*/api/v1/arbeidstaker/arbeidsforhold")
                        .and()
                        .method(HttpMethod.GET))
                .filters(f -> f.filter(authentication))
                .uri("https://modapp-" + env + ".adeo.no");
    }

    private Function<PredicateSpec, Buildable<Route>> createReadableRouteToNewEndpoint(String env, GatewayFilter authentication) {
        return spec -> spec
                .path("/" + env + "/api/v1/arbeidstaker/arbeidsforhold")
                .and().method(HttpMethod.GET)
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + env + "/(?<segment>.*)", "/api/v1/arbeidstaker/arbeidsforhold")
                        .filter(authentication)
                )
                .uri("https://aareg-services-" + env + ".dev.intern.nav.no");
    }

}
