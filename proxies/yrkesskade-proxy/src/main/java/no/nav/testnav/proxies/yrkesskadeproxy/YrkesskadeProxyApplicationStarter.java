package no.nav.testnav.proxies.yrkesskadeproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.proxies.yrkesskadeproxy.config.Consumers;
import no.nav.testnav.proxies.yrkesskadeproxy.consumer.FakedingsConsumer;
import no.nav.testnav.proxies.yrkesskadeproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
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

    @Bean("yrkesskadeRouteLocator")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           GatewayFilter tokenxAuthenticationFilter,
                                           Consumers consumers) {

        return builder
                .routes()
                .route(spec -> spec
                        .path("/**")
                        .and()
                        .not(not -> not.path("/internal/**"))
                        .filters(f -> f.filter(tokenxAuthenticationFilter))
                        .uri(consumers.getYrkesskade().getUrl()))
                .build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(YrkesskadeProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean("yrkesskadeTokenxAuthenticationFilter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    GatewayFilter tokenxAuthenticationFilter(
            TokenXService tokenService,
            FakedingsConsumer fakedingsConsumer,
            Consumers consumers) {

        return AddAuthenticationRequestGatewayFilterFactory
                .bearerIdportenHeaderFilter(fakedingsConsumer, tokenService, consumers.getYrkesskade());
    }

}