package no.nav.testnav.proxies.pensjontestdatafacadeproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.proxies.pensjontestdatafacadeproxy.config.Consumers;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;

@Import({
        CoreConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class PensjonTestdataFacadeProxyApplicationStarter {

    private static final String[] MILJOER = {"q1", "q2"};

    public static void main(String[] args) {
        new SpringApplicationBuilder(PensjonTestdataFacadeProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           AzureTrygdeetatenTokenService tokenService,
                                           Consumers consumers) {
        var routes = builder.routes();
        Arrays
                .stream(MILJOER)
                .forEach(
                        miljoe -> {
                            routes.route(
                                    spec -> spec
                                            .path("/" + miljoe + "/api/samboer/**")
                                            .filters(filterSPec -> filterSPec.filter(getAuthenticationFilter(tokenService,
                                                            consumers.getSamboerTestdata().getMiljoe(miljoe)))
                                                    .rewritePath("/" + miljoe + "/(?<segment>.*)", "/${segment}"))
                                            .uri(consumers.getSamboerTestdata().getMiljoe(miljoe).getUrl()));

                            routes.route(
                                    spec -> spec
                                            .path("/" + miljoe + "/api/mock-oppsett/**")
                                            .filters(filterSpec -> filterSpec.filter(getAuthenticationFilter(tokenService,
                                                            consumers.getAfpOffentlig().getMiljoe(miljoe)))
                                                    .rewritePath("/" + miljoe + "/(?<segment>.*)", "/${segment}"))
                                            .uri(consumers.getAfpOffentlig().getMiljoe(miljoe).getUrl()));
                        });
        routes
                .route(
                        spec -> spec
                                .path("/api/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec
                                        .addRequestHeader(HttpHeaders.AUTHORIZATION, "dolly")
                                ) //Auth header er required men sjekkes ikke utover det
                                .uri(consumers.getPensjonTestdataFacade().getUrl()))
                .build();

        return routes.build();
    }

    private GatewayFilter getAuthenticationFilter(AzureTrygdeetatenTokenService tokenService,
                                                  ServerProperties serverProperties) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenService
                                .exchange(serverProperties)
                                .map(AccessToken::getTokenValue));
    }

}