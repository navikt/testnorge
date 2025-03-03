package no.nav.testnav.proxies.kontoregisterperson;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.kontoregisterperson.config.Consumers;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
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
public class KontoregisterProxyApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(KontoregisterProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            AzureTrygdeetatenTokenService tokenService,
            Consumers consumers
    ) {
        var addAuthenticationHeaderDevFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenService
                                .exchange(consumers.getKontoregister())
                                .map(AccessToken::getTokenValue));
        return builder
                .routes()
                .route(
                        spec -> spec
                                .path("/**")
                                .and()
                                .not(not -> not.path("/internal/**"))
                                .filters(filterSpec -> filterSpec.filter(addAuthenticationHeaderDevFilter))
                                .uri(consumers.getKontoregister().getUrl()))
                .build();
    }

}