package no.nav.testnav.proxies.udistubproxy;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
@RequiredArgsConstructor
public class UdistubProxyApplicationStarter {

    private final TokenExchange tokenExchange;
    private final Consumers consumers;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        var addAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenExchange
                                .exchange(consumers.getTestnavUdistub())
                                .map(AccessToken::getTokenValue));
        return builder
                .routes()
                .route(spec -> spec.path("/**")
                        .filters(
                                filterspec -> filterspec
                                        .setResponseHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
                                        .filter(addAuthenticationHeaderFilter))
                        .uri(consumers.getTestnavUdistub().getUrl()))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(UdistubProxyApplicationStarter.class, args);
    }

}
