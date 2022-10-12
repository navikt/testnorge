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
import java.util.stream.Stream;

@Import({
    CoreConfig.class,
    DevConfig.class,
    SecurityConfig.class
})
@SpringBootApplication
public class AaregProxyApplicationStarter {

    @Value("${app.modapp.uri.pattern:https://aareg-services-{{ENV}}.dev.intern.nav.no}")
    private String modappUriPattern;

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
        RouteLocatorBuilder.Builder routes = builder.routes();

        var preprodFilter = AddAuthenticationRequestGatewayFilterFactory
            .bearerAuthenticationAndNavConsumerTokenHeaderFilter(stsPreprodOidcTokenService::getToken);
        Stream.of("q1", "q2", "q4", "q5")
            .forEach(env -> routes
                .route(createRoute(env, preprodFilter))
                .route(env, createQueryBasedRoute(env, preprodFilter))
                .route(env, createPathBasedRoute(env, preprodFilter)));

        var testFilter = AddAuthenticationRequestGatewayFilterFactory
            .bearerAuthenticationAndNavConsumerTokenHeaderFilter(stsTestOidcTokenService::getToken);
        Stream.of("t0", "t1", "t2", "t3", "t4", "t5")
            .forEach(env -> routes
                .route(createRoute(env, testFilter))
                .route(env, createQueryBasedRoute(env, testFilter))
                .route(env, createPathBasedRoute(env, testFilter)));

        return routes.build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String miljo, GatewayFilter filter) {
        return spec -> spec
            .path("/api/" + miljo + "/**")
            .filters(filterSpec -> filterSpec
                .rewritePath("/api/" + miljo + "/(?<segment>.*)", "/aareg-services/api/${segment}")
                .filter(filter)
            ).uri("https://modapp-" + miljo + ".adeo.no");
    }

    private Function<PredicateSpec, Buildable<Route>> createQueryBasedRoute(String env, GatewayFilter addRequiredAuthHeaders) {
        return spec -> spec
            .path("/api/v1/**")
            .and()
            .query("miljoe", env)
            .filters(f -> f
                .rewritePath("^(/api/v1/)", "/aareg-services/api/v1/")
                .removeRequestParameter("miljoe")
                .addRequestHeader("miljoe", env)
                .filter(addRequiredAuthHeaders))
            .uri(modappUriPattern.replace("{{ENV}}", env));
    }

    private Function<PredicateSpec, Buildable<Route>> createPathBasedRoute(String env, GatewayFilter addRequiredAuthHeaders) {
        return spec -> spec
            .path("/api/v1/**")
            .and()
            .predicate(p -> p.getRequest().getPath().value().endsWith("/miljoe/" + env))
            .filters(f -> f
                .rewritePath("^(/api/v1/)", "/aareg-services/api/v1/")
                .rewritePath("/miljoe/" + env + "$", "")
                .addRequestHeader("miljoe", env)
                .filter(addRequiredAuthHeaders))
            .uri(modappUriPattern.replace("{{ENV}}", env));
    }

}