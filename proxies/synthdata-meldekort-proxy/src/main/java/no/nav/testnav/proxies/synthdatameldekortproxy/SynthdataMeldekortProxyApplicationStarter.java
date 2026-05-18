package no.nav.testnav.proxies.synthdatameldekortproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.synthdatameldekortproxy.config.Consumers;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
public class SynthdataMeldekortProxyApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SynthdataMeldekortProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean("synthdataMeldekortRouteLocator")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            AzureNavTokenService tokenService,
            Consumers consumers
    ) {
        var addAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenService
                                .exchange(consumers.getSyntMeldekort())
                                .map(AccessToken::getTokenValue));
        return builder
                .routes()
                .route(
                        spec -> spec
                                .path("/**")
                                .and()
                                .not(not -> not.path("/internal/**"))
                                .filters(filterSpec -> filterSpec.filter(addAuthenticationHeaderFilter))
                                .uri(consumers.getSyntMeldekort().getUrl()))
                .build();
    }

}