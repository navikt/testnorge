package no.nav.testnav.proxies.dokarkivproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.dokarkivproxy.config.credentials.DokarkivProperties;
import no.nav.testnav.proxies.dokarkivproxy.config.credentials.DokarkivQ1Properties;
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

@Import({
        CoreConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class DokarkivProxyApplicationStarter {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           TrygdeetatenAzureAdTokenService tokenService,
                                           DokarkivProperties dokarkivProperties,
                                           DokarkivQ1Properties dokarkivQ1Properties) {

        var addAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(dokarkivProperties)
                        .map(AccessToken::getTokenValue));

        var addAuthenticationHeaderQ1Filter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(dokarkivQ1Properties)
                        .map(AccessToken::getTokenValue));

        return builder
                .routes()
                .route(createRoute("q1", addAuthenticationHeaderQ1Filter))
                .route(createRoute("q2", addAuthenticationHeaderFilter))
                .route(createRoute("q4", addAuthenticationHeaderFilter))
                .route(createRoute("q5", addAuthenticationHeaderFilter))
                .route(createRoute("qx", addAuthenticationHeaderFilter))
                .route(createRoute("t3", addAuthenticationHeaderFilter))
                .route(createRoute("t13", addAuthenticationHeaderFilter))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DokarkivProxyApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, GatewayFilter filter) {
        return spec -> spec
                .path("/api/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/api/" + miljo + "/(?<segment>.*)", "/rest/journalpostapi/${segment}")
                        .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                        .filter(filter)
                ).uri("https://dokarkiv-" + miljo + ".dev.adeo.no");
    }
}
