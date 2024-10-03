package no.nav.testnav.proxies.yrkesskadeproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.proxies.yrkesskadeproxy.config.Consumers;
import no.nav.testnav.proxies.yrkesskadeproxy.consumer.FakedingsConsumer;
import no.nav.testnav.proxies.yrkesskadeproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecurityConfig.class,
})
@SpringBootApplication
public class YrkesskadeProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(YrkesskadeProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           GatewayFilter tokenxAuthenticationFilter,
                                           Consumers consumers) {

        return builder
                .routes()
                .route(spec -> spec
                        .path("/**")
                        .filters(f -> f.filter(tokenxAuthenticationFilter))
                        .uri(consumers.getYrkesskade().getUrl()))
                .build();
    }

    @Bean
    GatewayFilter tokenxAuthenticationFilter(
            TokenXService tokenService,
            FakedingsConsumer fakedingsConsumer,
            Consumers consumers) {

        return AddAuthenticationRequestGatewayFilterFactory
                .bearerIdportenHeaderFilter(fakedingsConsumer, tokenService, consumers.getYrkesskade());
    }
}