package no.nav.testnav.proxies.dokarkivproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.proxies.dokarkivproxy.config.Consumers;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.function.Function;

@Import({
        CoreConfig.class,
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class DokarkivProxyApplicationStarter {

    private static final String[] miljoer = new String[]{"q1", "q2", "q4"};

    @Bean("dokarkivRouteLocator")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            AzureTrygdeetatenTokenService tokenService,
            Consumers consumers) {
        var routes = builder.routes();
        Arrays
                .asList(miljoer)
                .forEach(
                        miljoe -> routes.route(createRoute(miljoe, forEnvironment(consumers.getDokarkiv(), miljoe).getUrl(),
                                AddAuthenticationRequestGatewayFilterFactory
                                        .bearerAuthenticationHeaderFilter(
                                                () -> tokenService
                                                        .exchange(forEnvironment(consumers.getDokarkiv(), miljoe))
                                                        .map(AccessToken::getTokenValue)))));
        return routes.build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(DokarkivProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, String url, GatewayFilter filter) {
        return spec -> spec
                .path("/api/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/api/" + miljo + "/(?<segment>.*)", "/rest/journalpostapi/${segment}")
                        .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                        .filter(filter)
                ).uri(url);
    }

    private static ServerProperties forEnvironment(ServerProperties original, String env) {
        var replacement = "q2".equals(env) ? "" : '-' + env;
        return ServerProperties.of(
                original.getCluster(),
                original.getNamespace(),
                original.getName().replace("-MILJOE", replacement),
                original.getUrl().replace("-MILJOE", replacement)
        );
    }
}