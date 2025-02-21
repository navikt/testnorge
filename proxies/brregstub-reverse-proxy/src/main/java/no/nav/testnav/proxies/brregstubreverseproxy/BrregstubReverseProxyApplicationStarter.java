package no.nav.testnav.proxies.brregstubreverseproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.brregstubreverseproxy.config.Consumers;
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
public class BrregstubReverseProxyApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BrregstubReverseProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
//                                           AzureNavTokenService tokenService,
                                           Consumers consumers) {

//        var addAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
//                .bearerAuthenticationHeaderFilter(
//                        () -> tokenService
//                                .exchange(consumers.getBrregstub())
//                                .map(AccessToken::getTokenValue));

        return builder.routes()
                .route(spec -> spec.path("/**")
//                        .filters(filter -> filter.filter(addAuthenticationHeaderFilter))
                        .uri(consumers.getBrregstub().getUrl())
                )
                .build();
    }
}