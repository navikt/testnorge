package no.nav.testnav.proxies.krrstubproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.proxies.krrstubproxy.config.credentials.KrrStubProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class KrrstubProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(KrrstubProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, TokenExchange tokenExchange, KrrStubProperties properties) {

        return builder.routes()
                .route(spec -> spec.path("**/v1/**").uri(properties.getUrl()))
                .route(spec -> spec.path("**/v2/**")
                        .filters(filterSpec -> filterSpec
                                .filter(AddAuthenticationRequestGatewayFilterFactory
                                        .createAuthenticationHeaderFilter(() ->
                                                tokenExchange.generateToken(properties).map(token -> token.getTokenValue()))))
                        .uri(properties.getUrl()))
                .build();
    }
}
