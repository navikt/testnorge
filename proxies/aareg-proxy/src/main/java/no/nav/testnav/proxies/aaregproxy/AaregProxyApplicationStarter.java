package no.nav.testnav.proxies.aaregproxy;

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

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class AaregProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(AaregProxyApplicationStarter.class, args);
    }

    @Bean
    public StsOidcTokenService stsPreprodOidcTokenService(
            @Value("${sts.preprod.token.provider.url}") String url,
            @Value("${sts.preprod.token.provider.username}") String username,
            @Value("${sts.preprod.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(url, username, password);
    }

    @Bean
    public StsOidcTokenService stsTestOidcTokenService(
            @Value("${sts.test.token.provider.url}") String url,
            @Value("${sts.test.token.provider.username}") String username,
            @Value("${sts.test.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(url, username, password);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, StsOidcTokenService stsTestOidcTokenService, StsOidcTokenService stsPreprodOidcTokenService) {

        var preprodFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(stsPreprodOidcTokenService::getToken);
        var testFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(stsTestOidcTokenService::getToken);
        return builder
                .routes()
                .route(createRoute("q0", preprodFilter))
                .route(createRoute("q1", preprodFilter))
                .route(createRoute("q2", preprodFilter))
                .route(createRoute("q4", preprodFilter))
                .route(createRoute("q5", preprodFilter))
                .route(createRoute("qx", preprodFilter))
                .route(createRoute("t0", testFilter))
                .route(createRoute("t1", testFilter))
                .route(createRoute("t2", testFilter))
                .route(createRoute("t3", testFilter))
                .route(createRoute("t4", testFilter))
                .route(createRoute("t5", testFilter))
                .route(createRoute("t6", testFilter))
                .route(createRoute("t13", testFilter))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, GatewayFilter filter) {
        return spec -> spec
                .path("/api/" + miljo + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/api/" + miljo + "/(?<segment>.*)", "/aareg-services/api/${segment}")
                        .filter(filter)
                ).uri("https://modapp-" + miljo + ".adeo.no");
    }
}