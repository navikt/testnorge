package no.nav.testnav.proxies.udistubproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.udistubproxy.credentials.UdistubServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

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

    public UdistubProxyApplicationStarter(TokenExchange tokenExchange, UdistubServiceProperties udistubServiceProperties) {
        this.tokenExchange = tokenExchange;
        this.udistubServiceProperties = udistubServiceProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(UdistubProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        var addAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenExchange.exchange(udistubServiceProperties).map(AccessToken::getTokenValue));

        return builder
                .routes()
                .route(spec -> spec.path("/**")
                        .filters(filterspec -> filterspec
                                .setResponseHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
                                .filter(addAuthenticationHeaderFilter))
                        .uri(udistubServiceProperties.getUrl()))
                .build();
    }
}