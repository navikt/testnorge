package no.nav.testnav.apps.endringsmeldingfrontend;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.apps.endringsmeldingfrontend.config.Consumers;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesessionsecurity.config.OidcInMemorySessionConfiguration;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
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
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import java.util.function.Function;

@Import({
        CoreConfig.class,
        OidcInMemorySessionConfiguration.class,
        FrontendConfig.class
})
@SpringBootApplication
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class EndringsmeldingFrontendApplicationStarter {

    private final Consumers consumers;
    private final TokenExchange tokenExchange;

    public static void main(String[] args) {
        new SpringApplicationBuilder(EndringsmeldingFrontendApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(consumers.getEndringsmeldingService(), "endringsmelding-service"))
                .route(createRoute(consumers.getTestnorgeProfilApi()))
                .build();
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
                .apply(exchange -> {
                    return tokenExchange
                            .exchange(serverProperties, exchange)
                            .map(AccessToken::getTokenValue);
                });
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(ServerProperties serverProperties) {
        var segment = serverProperties.getName();
        var host = serverProperties.getUrl();
        var filter = addAuthenticationHeaderFilterFrom(serverProperties);
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(ServerProperties serverProperties, String segment) {
        return createRoute(
                segment,
                serverProperties.getUrl(),
                addAuthenticationHeaderFilterFrom(serverProperties)
        );
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host, GatewayFilter filter) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filters(filter)
                ).uri(host);
    }
}