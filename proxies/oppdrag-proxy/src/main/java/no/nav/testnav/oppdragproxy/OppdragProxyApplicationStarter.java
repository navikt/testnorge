package no.nav.testnav.oppdragproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
})
@SpringBootApplication
public class OppdragProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(OppdragProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder
                .routes()
                .route(createRoute("q1", "9210"))
                .route(createRoute("q2", "9234"))
                .route(createRoute("q4", "9236"))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljoe, String port) {

        return predicateSpec -> predicateSpec
                .path("/" + miljoe + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + miljoe + "/(?<segment>.*)", "/${segment}")
                )
                .uri("http://ztest.test.local:" + port);
    }
}
