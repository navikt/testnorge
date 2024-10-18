package no.nav.testnav.proxies.fullmaktproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.proxies.fullmaktproxy.config.Consumers;
import no.nav.testnav.proxies.fullmaktproxy.consumer.FakedingsConsumer;
import no.nav.testnav.proxies.fullmaktproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
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
})
@SpringBootApplication
public class FullmaktProxyApplicationStarter {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           Consumers consumers,
                                           FakedingsConsumer fakedingsConsumer,
                                           TokenXService tokenXService) {
        return builder
                .routes()
                .route(createRoute(
                        consumers
                                .getFullmakt()
                                .getUrl(),
                        AddAuthenticationRequestGatewayFilterFactory
                                .bearerIdportenHeaderFilter(fakedingsConsumer, tokenXService, consumers.getFullmakt())))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FullmaktProxyApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String url, GatewayFilter filter) {
        return spec -> spec
                .path("/**")
                .filters(
                        filterSpec -> filterSpec
                                .rewritePath("/(?<segment>.*)", "/${segment}")
                                .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                                .filter(filter))
                .uri(url);
    }
}