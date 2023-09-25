package no.nav.testnav.proxies.sykemeldingapiproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.sykemeldingapiproxy.config.SykemeldingApiProperties;
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
public class SykemeldingApiProxyApplicationStarter {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           AzureAdTokenService tokenService,
                                           SykemeldingApiProperties sykemeldingApiProperties) {

        return builder
                .routes()
                .route(createRoute("sykemelding", "http://testnorge-sykemelding-api.dolly.svc.nais.local",
                        AddAuthenticationRequestGatewayFilterFactory
                                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(sykemeldingApiProperties)
                                        .map(AccessToken::getTokenValue))))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SykemeldingApiProxyApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host, GatewayFilter filter) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }
}