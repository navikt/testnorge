package no.nav.testnav.proxies.safproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
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

import java.util.Arrays;
import java.util.function.Function;

@Import({
        CoreConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class SafProxyApplicationStarter {

    private static final String[] miljoer = new String[]{"q1", "q2", "q4", "q5", "t3"};

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            TrygdeetatenAzureAdTokenService tokenService,
            Consumers consumers
    ) {
        var routes = builder.routes();
        Arrays
                .asList(miljoer)
                .forEach(
                        miljoe -> {
                            var properties = forEnvironment(consumers.getSaf(), miljoe);
                            routes
                                    .route(createRoute(miljoe, properties.getUrl(),
                                            AddAuthenticationRequestGatewayFilterFactory
                                                    .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(properties)
                                                            .map(AccessToken::getTokenValue))));
                        });
        return routes.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SafProxyApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, String url, GatewayFilter filter) {
        return spec -> spec
                .path("/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + miljo + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(url);
    }

    private static ServerProperties forEnvironment(ServerProperties original, String env) {
        var replacement = "q2".equals(env) ? "" : '-' + env;
        return ServerProperties.of(
                original.getCluster(),
                original.getNamespace(),
                original.getName().replace("-MILJOE", replacement),
                original.getUrl().replace("-MILJOE", replacement)
        );
    }

}
