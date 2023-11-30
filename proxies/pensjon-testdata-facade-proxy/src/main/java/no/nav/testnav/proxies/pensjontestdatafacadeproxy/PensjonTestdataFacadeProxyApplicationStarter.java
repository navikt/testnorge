package no.nav.testnav.proxies.pensjontestdatafacadeproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.proxies.pensjontestdatafacadeproxy.config.Consumers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class PensjonTestdataFacadeProxyApplicationStarter {

    private static final String[] MILJOER = {"q1", "q2"};

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           TrygdeetatenAzureAdTokenService tokenService,
                                           Consumers consumers) {
        var routes = builder.routes();
        Arrays
                .stream(MILJOER)
                .forEach(
                        miljoe ->
                                routes.route(
                                        spec -> spec
                                                .path("/" + miljoe + "/api/samboer/**")
                                                .filters(filterSPec -> filterSPec.filter(getAuthenticationFilter(tokenService, consumers.getSamboerTestdata(), miljoe))
                                                        .rewritePath("/" + miljoe + "/(?<segment>.*)", "/${segment}"))
                                                .uri(forEnvironment(consumers.getSamboerTestdata(), miljoe).getUrl())));
        routes
                .route(
                        spec -> spec
                                .path("/api/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec
                                        .addRequestHeader(HttpHeaders.AUTHORIZATION, "dolly")
                                ) //Auth header er required men sjekkes ikke utover det
                                .uri("http://pensjon-testdata-facade.pensjontestdata.svc.nais.local/"))
                .build();

        return routes.build();
    }

    private GatewayFilter getAuthenticationFilter(TrygdeetatenAzureAdTokenService tokenService,
                                                  ServerProperties serverProperties,
                                                  String miljoe) {
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenService
                                .exchange(forEnvironment(serverProperties, miljoe))
                                .map(AccessToken::getTokenValue));
    }

    private static ServerProperties forEnvironment(ServerProperties original, String env) {
        return ServerProperties.of(
                original.getCluster(),
                original.getNamespace(),
                original.getName().replace("MILJOE", env),
                original.getUrl().replace("MILJOE", env + ("q1".equals(env) ? ".very" : ""))
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(PensjonTestdataFacadeProxyApplicationStarter.class, args);
    }

}