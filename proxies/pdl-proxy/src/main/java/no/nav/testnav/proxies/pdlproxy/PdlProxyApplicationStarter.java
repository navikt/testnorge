package no.nav.testnav.proxies.pdlproxy;

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
public class PdlProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(PdlProxyApplicationStarter.class, args);
    }

    @Bean
    public StsOidcTokenService stsOidcTokenService(
            @Value("${sts.token.provider.url}") String url,
            @Value("${sts.token.provider.username}") String username,
            @Value("${sts.token.provider.password}") String password) {

        return new StsOidcTokenService(url, username, password);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, StsOidcTokenService stsOidcTokenService,
                                           @Value("${hendelse.lager.api.key}") String hendelselagerApiKey,
                                           @Value("${person.aktor.admin.api}") String aktoerAdminApiKey) {

        var addAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(stsOidcTokenService::getToken);
        var addAuthorizationAndNavConsumerTokenToRouteFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationAndNavConsumerTokenHeaderFilter(stsOidcTokenService::getToken);
        var addHendelselagerApiKeyAuthenticationHeader = AddAuthenticationRequestGatewayFilterFactory
                .apiKeyAuthenticationHeaderFilter(hendelselagerApiKey);
        var addAktoerAdminApiKeyAuthenticationHeader = AddAuthenticationRequestGatewayFilterFactory
                .apiKeyAuthenticationHeaderFilter(aktoerAdminApiKey);


        return builder
                .routes()
                .route(createRoute("pdl-api", "https://pdl-api.dev.adeo.no", addAuthorizationAndNavConsumerTokenToRouteFilter))
                .route(createRoute("pdl-testdata", "https://pdl-testdata.dev.adeo.no", addAuthenticationHeaderFilter))
                .route(createRoute("pdl-identhendelse", "https://pdl-identhendelse-lager.dev.intern.nav.no", addHendelselagerApiKeyAuthenticationHeader))
                .route(createRoute("pdl-npid", "https://pdl-aktor.dev.intern.nav.no", addAktoerAdminApiKeyAuthenticationHeader))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> createRoute(String segment, String host, GatewayFilter filter) {
        return spec -> spec
                .path("/" + segment + "/**")
                .filters(filterSpec -> filterSpec
                        .rewritePath("/" + segment + "/(?<segment>.*)", "/${segment}")
                        .filter(filter)
                ).uri(host);
    }
}
