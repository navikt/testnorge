package no.nav.testnav.proxies.arbeidssoekerregistreringproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.arbeidssoekerregistreringproxy.config.Consumers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class ArbeidssoekerregistreringProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(ArbeidssoekerregistreringProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           AzureTrygdeetatenTokenService tokenService,
                                           Consumers consumers) {

        return builder.routes()
                .route(spec -> spec.path("/**")
                        .filters(AddAuthenticationRequestGatewayFilterFactory.bearerAuthenticationHeaderFilter(
                                () -> tokenService.exchange(consumers.getArbeidssoekerregistrering())
                                        .map(AccessToken::getTokenValue)
                        ))
                        .uri("https://dolly-arbeidssoekerregisteret.intern.dev.nav.no/")
                )
                .build();
    }
}