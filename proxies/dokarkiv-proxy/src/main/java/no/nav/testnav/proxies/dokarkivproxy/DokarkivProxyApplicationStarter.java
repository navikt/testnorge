package no.nav.testnav.proxies.dokarkivproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.dokarkivproxy.config.credentials.DokarkivProperties;
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

import java.util.Arrays;
import java.util.function.Function;

@Import({
        CoreConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class DokarkivProxyApplicationStarter {

    private static final String[] miljoer = new String[]{"q1", "q2", "q4", "q5", "qx", "t3", "t13"};

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           TrygdeetatenAzureAdTokenService tokenService,
                                           DokarkivProperties dokarkivProperties) {

        var routes = builder.routes();
        Arrays.asList(miljoer)
                .forEach(miljoe -> routes.route(createRoute(miljoe, dokarkivProperties.forEnvironment(miljoe).getUrl(),
                        AddAuthenticationRequestGatewayFilterFactory
                        .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(dokarkivProperties.forEnvironment(miljoe))
                                .map(AccessToken::getTokenValue)))));

        return routes.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DokarkivProxyApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, String url, GatewayFilter filter) {
        return spec -> spec
                .path("/api/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/api/" + miljo + "/(?<segment>.*)", "/rest/journalpostapi/${segment}")
                        .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                        .filter(filter)
                ).uri(url);
    }
}
