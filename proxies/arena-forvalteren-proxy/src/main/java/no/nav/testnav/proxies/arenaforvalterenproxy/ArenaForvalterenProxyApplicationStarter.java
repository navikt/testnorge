package no.nav.testnav.proxies.arenaforvalterenproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
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
public class ArenaForvalterenProxyApplicationStarter {

    private static final String[] ARENA_MILJOER = {"q1", "q2", "q4"};

    public static void main(String[] args) {
        new SpringApplicationBuilder(ArenaForvalterenProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            Consumers consumers
    ) {
        var routes = builder
                .routes()
                .route(
                        spec -> spec
                                .path("/api/**")
                                .uri("http://arena-forvalteren.teamarenanais.svc.nais.local/"));
        Arrays
                .stream(ARENA_MILJOER)
                .forEach(env -> routes.route(createRoute(env, forEnvironment(consumers.getArenaServices(), env).getUrl())));
        return routes.build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                ).uri(host);
    }

    private static ServerProperties forEnvironment(ServerProperties original, String env) {
        return ServerProperties.of(
                original.getCluster(),
                original.getNamespace(),
                original.getName().replace("MILJOE", env),
                original.getUrl().replace("MILJOE", env)
        );
    }

}
