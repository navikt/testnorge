package no.nav.testnav.proxies.udistubproxy;

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

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.udistubproxy.credentials.UdistubDevServiceProperties;
import no.nav.testnav.proxies.udistubproxy.credentials.UdistubServiceProperties;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class UdistubProxyApplicationStarter {
    private final TokenExchange tokenExchange;
    private final UdistubServiceProperties udistubServiceProperties;
    private final UdistubDevServiceProperties udistubDevServiceProperties;

    public UdistubProxyApplicationStarter(TokenExchange tokenExchange, UdistubServiceProperties udistubServiceProperties, UdistubDevServiceProperties udistubDevServiceProperties) {
        this.tokenExchange = tokenExchange;
        this.udistubServiceProperties = udistubServiceProperties;
        this.udistubDevServiceProperties = udistubDevServiceProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(UdistubProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        var addAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenExchange.exchange(udistubServiceProperties).map(AccessToken::getTokenValue));

        var addAuthenticationHeaderDevFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenExchange.exchange(udistubDevServiceProperties).map(AccessToken::getTokenValue));

        return builder
                .routes()
                .route(createRoute("udistub", "https://udi-stub.dev.intern.nav.no", addAuthenticationHeaderFilter))
                .route(createRoute("udistub-dev", "https://udi-stub-dev.dev.intern.nav.no", addAuthenticationHeaderDevFilter))
                .build();
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