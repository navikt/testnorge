package no.nav.testnav.altinn3tilgangproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.altinn3tilgangproxy.config.Consumers;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class Altinn3TilgangProxyApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Altinn3TilgangProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean("altinn3TilgangRouteLocator")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            TokenExchange tokenExchange,
            Consumers consumers
    ) {
        return builder
                .routes()
                .route(spec -> spec
                        .path("/**")
                        .and()
                        .not(not -> not.path("/internal/**"))
                        .filters(filterSpec -> filterSpec
                                .filter(AddAuthenticationRequestGatewayFilterFactory
                                        .bearerAuthenticationHeaderFilter(
                                                () -> tokenExchange
                                                        .exchange(consumers.getAltinn3TilgangService())
                                                        .map(AccessToken::getTokenValue))))
                        .uri(consumers.getAltinn3TilgangService().getUrl()))
                .build();
    }

}
