package no.nav.testnav.proxies.arbeidsplassencvproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.arbeidsplassencvproxy.config.ArbeidsplassenCVProperties;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.FakedingsConsumer;
import no.nav.testnav.proxies.arbeidsplassencvproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
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
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class ArbeidsplassenCVProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ArbeidsplassenCVProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           TrygdeetatenAzureAdTokenService tokenService,
                                           ArbeidsplassenCVProperties arbeidsplassenCVProperties,
                                           FakedingsConsumer fakedingsConsumer,
                                           TokenXService tokenXService) {

        return builder.routes()
                .route(createRoute(arbeidsplassenCVProperties.getUrl(),
                        AddAuthenticationRequestGatewayFilterFactory
                                .bearerIdportenHeaderFilter(fakedingsConsumer, tokenXService,
                                        () -> tokenService.exchange(arbeidsplassenCVProperties)
                                        .map(AccessToken::getTokenValue))))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String url, GatewayFilter filter) {
        return spec -> spec
                .path("/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/(?<segment>.*)", "/pam-cv-api/${segment}")
                        .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                        .filter(filter)
                ).uri(url);
    }
}