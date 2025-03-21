package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
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

import java.util.Arrays;
import java.util.function.Function;

@Import({
        SecureOAuth2ServerToServerConfiguration.class,
        SecurityConfig.class
})
@Configuration
public class RouteLocatorConfig {

    private static final String[] MILJOER = { "q1", "q2", "q4"};

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            AzureTrygdeetatenTokenService tokenService,
            Consumers consumers) {

        var routes = builder.routes();
        Arrays.stream(MILJOER)
                .forEach(env -> {
                    var readableAuthentication =
                            getAuthenticationFilter(tokenService, forEnvironment(consumers.getAaregServices(), env));
                    var writeableAuthentication =
                            getAuthenticationFilter(tokenService, forEnvironment(consumers.getAaregVedlikehold(), env));

                    routes
                            .route(createReadableRouteToNewEndpoint(env, readableAuthentication))
                            .route(createWriteableRouteToNewEndpoint(env, writeableAuthentication));
                });
        return routes.build();
    }

    private GatewayFilter getAuthenticationFilter(AzureTrygdeetatenTokenService tokenService, ServerProperties serverProperties) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService
                        .exchange(serverProperties)
                        .map(AccessToken::getTokenValue));
    }

    private static ServerProperties forEnvironment(ServerProperties original, String environment) {
        var replacement = "q2".equals(environment) ? "" : '-' + environment;
        return ServerProperties.of(
                original.getCluster(),
                original.getNamespace(),
                original.getName().replace("-{env}", replacement),
                original.getUrl().replace("-{env}", replacement)
        );
    }

    private Function<PredicateSpec, Buildable<Route>> createReadableRouteToNewEndpoint(String miljoe, GatewayFilter authentication) {

        return predicateSpec -> predicateSpec
                .path("/" + miljoe + "/**")
                .and()
                .method(HttpMethod.GET)
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + miljoe + "/(?<segment>.*)", "/${segment}")
                        .filter(authentication)
                )
                .uri("https://aareg-services-" + miljoe + ".dev.intern.nav.no");
    }

    private Function<PredicateSpec, Buildable<Route>> createWriteableRouteToNewEndpoint(String miljoe, GatewayFilter authentication) {

        return predicateSpec -> predicateSpec
                .path("/" + miljoe + "/**")
                .and()
                .method(HttpMethod.POST, HttpMethod.PUT)
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + miljoe + "/(?<segment>.*)", "/${segment}")
                        .filter(authentication)
                )
                .uri("https://aareg-vedlikehold-" + miljoe + ".dev.intern.nav.no");
    }

}
