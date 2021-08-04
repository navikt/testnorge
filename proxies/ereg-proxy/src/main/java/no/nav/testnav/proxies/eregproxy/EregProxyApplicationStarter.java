package no.nav.testnav.proxies.eregproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.function.Function;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class EregProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(EregProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute("q0"))
                .route(createRoute("q1"))
                .route(createRoute("q2"))
                .route(createRoute("q4"))
                .route(createRoute("q5"))
                .route(createRoute("qx"))
                .route(createRoute("t0"))
                .route(createRoute("t1"))
                .route(createRoute("t2"))
                .route(createRoute("t3"))
                .route(createRoute("t4"))
                .route(createRoute("t5"))
                .route(createRoute("t6"))
                .route(createRoute("t13"))
                .build();
    }

    private Function<PredicateSpec, Route.AsyncBuilder> createRoute(String miljo) {
        return spec -> spec
                .path("/api/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/api/" + miljo + "/(?<segment>.*)", "/ereg/api/${segment}")
                ).uri("https://modapp-" + miljo + ".adeo.no/ereg/api/");
    }

}