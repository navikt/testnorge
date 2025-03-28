package no.nav.testnav.apps.tpsmessagingfrontend;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.apps.tpsmessagingfrontend.config.Consumers;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivefrontend.config.FrontendConfig;
import no.nav.testnav.libs.reactivefrontend.filter.AddAuthenticationHeaderToRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
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

import java.util.function.Function;

@Import({
        CoreConfig.class,
        FrontendConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
})
@SpringBootApplication
@RequiredArgsConstructor
public class TpsMessagingFrontendApplicationStarter {

    private final Consumers consumers;
    private final TokenExchange tokenExchange;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(createRoute(consumers.getTpsMessagingService(), "tps-messaging-service"))
                .route(createRoute(consumers.getTestnorgeProfilApi()))
                .build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(TpsMessagingFrontendApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    private GatewayFilter addAuthenticationHeaderFilterFrom(ServerProperties serverProperties) {
        return new AddAuthenticationHeaderToRequestGatewayFilterFactory()
                .apply(exchange -> {
                    return tokenExchange
                            .exchange(serverProperties)
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