package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
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

    private static final String[] ENV = {"q1", "q2", "q4", "q5", "qx", "t3", "t13"};

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            TrygdeetatenAzureAdTokenService tokenService,
            AaregProperties aaregProperties
    ) {
        var routes = builder.routes();
        Stream.of(ENV)
                .forEach(env -> {
                    var readableAuthentication = getAuthenticationFilter(tokenService, aaregProperties.services.forEnvironment(env));
                    var writeableAuthentication = getAuthenticationFilter(tokenService, aaregProperties.vedlikehold.forEnvironment(env));
                    routes
                            .route(createReadableRouteToNewEndpoint(env, readableAuthentication))
                            .route(createWriteableRouteToNewEndpoint(env, writeableAuthentication));
                });
        return routes.build();
    }

    private GatewayFilter getAuthenticationFilter(TrygdeetatenAzureAdTokenService tokenService, ServerProperties serverProperties) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService
                        .exchange(serverProperties)
                        .map(AccessToken::getTokenValue));
    }

    private Function<PredicateSpec, Buildable<Route>> createReadableRouteToNewEndpoint(String env, GatewayFilter authentication) {
        return predicateSpec -> predicateSpec
                .path("/" + env + "/api/v1/arbeidsforhold")
                .and()
                .method(HttpMethod.GET)
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + env + "/(?<segment>.*)", "/aareg-services/api/v1/arbeidsforhold/")
                        .filter(authentication)
                )
                .uri("https://aareg-services-" + env + ".dev.intern.nav.no");
    }

    private Function<PredicateSpec, Buildable<Route>> createWriteableRouteToNewEndpoint(String env, GatewayFilter authentication) {
        return predicateSpec -> predicateSpec
                .path("/" + env + "/api/v1/arbeidsforhold")
                .and()
                .method(HttpMethod.POST, HttpMethod.PUT)
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + env + "/(?<segment>.*)", "/aareg-vedlikehold/api/v1/arbeidsforhold/")
                        .filter(authentication)
                )
                .uri("https://aareg-vedlikehold-" + env + ".dev.intern.nav.no");
    }

}
