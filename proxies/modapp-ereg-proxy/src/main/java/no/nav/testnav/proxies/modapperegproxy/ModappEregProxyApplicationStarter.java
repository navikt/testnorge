package no.nav.testnav.proxies.modapperegproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class ModappEregProxyApplicationStarter {

    private static final String[] miljoer = new String[]{ "q1", "q2", "q4" };
    private static final Map<String, String> miljoeUrlSegment = Map.of(
            "q1", "05",
            "q2", "10",
            "q4", "09"
    );

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            Consumers consumers
    ) {
        var routes = builder.routes();
        Arrays
                .asList(miljoer)
                .forEach(
                        miljoe -> {
                            var properties = forEnvironment(consumers.getEregAura(), miljoeUrlSegment.get(miljoe));
                            routes
                                    .route(createRoute(miljoe, properties.getUrl()));
                        });
        return routes.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ModappEregProxyApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, String url) {
        return spec -> spec
                .path("/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + miljo + "/(?<segment>.*)", "/${segment}")
                ).uri(url);
    }

    private static ServerProperties forEnvironment(ServerProperties original, String urlSegment) {
        return ServerProperties.of(
                original.getCluster(),
                original.getNamespace(),
                original.getName(),
                original.getUrl().replace("-MILJOE", urlSegment)
        );
    }
}