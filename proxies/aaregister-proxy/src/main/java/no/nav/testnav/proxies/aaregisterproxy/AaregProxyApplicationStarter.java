package no.nav.testnav.proxies.aaregisterproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.function.Function;
import java.util.stream.Stream;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class AaregProxyApplicationStarter {

    @Bean
    public StsOidcTokenService stsPreprodOidcTokenService(
            @Value("${sts.preprod.token.provider.url}") String url,
            @Value("${sts.preprod.token.provider.username}") String username,
            @Value("${sts.preprod.token.provider.password}") String password) {

        return new StsOidcTokenService(url, username, password);
    }

    @Bean
    public StsOidcTokenService stsTestOidcTokenService(
            @Value("${sts.test.token.provider.url}") String url,
            @Value("${sts.test.token.provider.username}") String username,
            @Value("${sts.test.token.provider.password}") String password) {

        return new StsOidcTokenService(url, username, password);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, StsOidcTokenService stsTestOidcTokenService, StsOidcTokenService stsPreprodOidcTokenService) {

        var routes = builder.routes();
        var preprodFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(stsPreprodOidcTokenService::getToken);
        Stream.of("q1", "q2", "q4", "q5")
                .forEach(env -> routes
                        .route(createRoute(env, preprodFilter)));

        var testFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(stsTestOidcTokenService::getToken);
        routes
                .route(createRoute("t3", testFilter));

        return routes.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(AaregProxyApplicationStarter.class, args);
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, GatewayFilter filter) {

        return spec -> spec
                .path("/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + miljo + "/(?<segment>.*)", "/aareg-core/${segment}")
                        .filter(filter)
                ).uri("https://modapp-" + miljo + ".adeo.no");
    }
}