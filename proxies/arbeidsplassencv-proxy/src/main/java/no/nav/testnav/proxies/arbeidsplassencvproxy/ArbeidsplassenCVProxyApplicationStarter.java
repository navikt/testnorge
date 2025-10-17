package no.nav.testnav.proxies.arbeidsplassencvproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.proxies.arbeidsplassencvproxy.config.Consumers;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.FakedingsConsumer;
import no.nav.testnav.proxies.arbeidsplassencvproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
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
        SecurityConfig.class
})
@SpringBootApplication
public class ArbeidsplassenCVProxyApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ArbeidsplassenCVProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           Consumers consumers,
                                           FakedingsConsumer fakedingsConsumer,
                                           TokenXService tokenXService) {
        var gatewayFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerIdportenHeaderFilter(
                        fakedingsConsumer,
                        tokenXService,
                        consumers.getArbeidsplassenCv());
        return builder
                .routes()
                .route(createRoute(consumers.getArbeidsplassenCv().getUrl(), gatewayFilter))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String url, GatewayFilter filter) {
        return spec -> spec
                .path("/**")
                .and()
                .not(not -> not.path("/internal/**"))
                .filters(
                        filterSpec -> filterSpec
                                .rewritePath("/(?<segment>.*)", "/pam-cv-api/${segment}")
                                .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                                .filter(filter))
                .uri(url);
    }

}