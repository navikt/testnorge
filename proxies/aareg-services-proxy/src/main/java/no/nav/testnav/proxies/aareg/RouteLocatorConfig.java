package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;

import java.util.function.Function;
import java.util.stream.Stream;

@Import(SecurityConfig.class)
@Configuration
public class RouteLocatorConfig {

    @Autowired
    private CloudProperties cloudProperties;

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            //TrygdeetatenAzureAdTokenService tokenService,
            //OnpremProperties onpremProperties,
            //CloudProperties cloudProperties,
            @Qualifier("q") StsOidcTokenService qStsOidcTokenService,
            @Qualifier("t") StsOidcTokenService tStsOidcTokenService
    ) {

        var qAuthentication = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(qStsOidcTokenService::getToken);
        var tAuthentication = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(tStsOidcTokenService::getToken);

        RouteLocatorBuilder.Builder routes = builder.routes();
        Stream.of("q1", "q2", "q4", "q5")
                .forEach(env -> routes
                        .route(createWritableRouteToOldEndpoint(env, qAuthentication)));
        Stream.of("t0", "t1", "t3", "t4", "t5")
                .forEach(env -> routes
                        .route(createWritableRouteToOldEndpoint(env, tAuthentication)));

/*        var onpremAuthentication = AddAuthenticationRequestGatewayFilterFactory
            .bearerAuthenticationHeaderFilter(
                () -> tokenService
                    .exchange(onpremProperties)
                    .map(AccessToken::getTokenValue)
            );*/

        // FROM: https://modapp-{env}.adeo.no/aareg-core/api/arbeidstaker
        // TO:   https://aareg-services-{env}.dev.intern.nav.no/aareg-core/api/arbeidstaker
        Stream.of("q0", "q1", "q2")
                .forEach(env -> routes
                        .route(createReadableRouteToNewEndpoint(env))
                        .route(createWritableRouteToOldEndpoint(env, qAuthentication)));
        return routes.build();
        /*return builder
            .routes()
            .route(spec -> spec
                .path("/aareg-core/api/v1/arbeidstaker/arbeidsforhold/**")
                .and()
                .method(HttpMethod.GET)
                .and()
                .predicate(p -> p.getRequest().getPath().value().startsWith("/"))
                .filters(f -> f
                    .filter(onpremAuthentication)
                    .filter((exchange, chain) -> {
                    ServerHttpRequest oldRequest = exchange.getRequest();
                    addOriginalRequestUrl(exchange, oldRequest.getURI());
                    String oldPath = oldRequest.getURI().getRawPath();
                    String newPath = oldPath.replaceAll("CATO", "foo"); // TODO: Add path here
                    ServerHttpRequest newRequest = oldRequest
                        .mutate()
                        .path(newPath)
                        .build();
                    exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
                    return chain.filter(exchange.mutate().request(newRequest).build);
                }))
                .uri(cloudProperties.getUrl())
            )
            .build();*/
    }

    private Function<PredicateSpec, Buildable<Route>> createReadableRouteToNewEndpoint(String env) {
        return spec -> spec
                .path("/" + env + "/api/v1/arbeidstaker/arbeidsforhold")
                .and()
                .method(HttpMethod.GET)
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + env + "/(?<segment>.*)", "/api/v1/arbeidstaker/arbeidsforhold")
                )
                .uri(cloudProperties.getUrl().replace("{env}", env));
    }

    private Function<PredicateSpec, Buildable<Route>> createWritableRouteToOldEndpoint(String env, GatewayFilter authentication) {
        return spec -> spec
                .path("/" + env + "/api/v1/arbeidstaker/arbeidsforhold")
                .and().not(p -> p.method(HttpMethod.GET))
                .filters(f -> f.filter(authentication))
                .uri("https://modapp-" + env + ".adeo.no");
    }

    @Configuration
    @ConfigurationProperties(prefix = "aareg.fss")
    public static class OnpremProperties extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "aareg.gcp")
    public static class CloudProperties extends ServerProperties {
    }

}
