package no.nav.testnav.proxies.nomproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.nomproxy.config.Consumers;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class NomProxyApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(NomProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           AzureTrygdeetatenTokenService tokenService,
                                           Consumers consumers) {

        return builder
                .routes()
                .route(spec -> spec
                        .path("/**")
                        .and()
                        .not(not -> not.path("/internal/**"))
                        .filters(filterSpec -> filterSpec.filters(
                                AddAuthenticationRequestGatewayFilterFactory.bearerAuthenticationHeaderFilter(
                                        () -> tokenService
                                                .exchange(consumers.getNomApi())
                                                .map(AccessToken::getTokenValue))))
                        .uri(consumers.getNomApi().getUrl())
                )
                .build();
    }
}