package no.nav.testnav.proxies.pdlproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.proxies.pdlproxy.config.credentials.PdlApiProperties;
import no.nav.testnav.proxies.pdlproxy.config.credentials.PdlQ1ApiProperties;
import no.nav.testnav.proxies.pdlproxy.config.credentials.PdlTestdataProperties;
import no.nav.testnav.proxies.pdlproxy.dto.CredentialsHolder;
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
        SecurityConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class PdlProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(PdlProxyApplicationStarter.class, args);
    }

    @Bean
    public CredentialsHolder credentialsHolder(
            @Value("${hendelse.lager.api.key}") String hendelselagerApiKey,
            @Value("${person.aktor.admin.api}") String aktoerAdminApiKey,
            @Value("${elastic.username}") String elasticUsername,
            @Value("${elastic.password}") String elasticPassword) {

        return new CredentialsHolder(hendelselagerApiKey, aktoerAdminApiKey, elasticUsername, elasticPassword);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           CredentialsHolder credentialsHolder,
                                           TrygdeetatenAzureAdTokenService tokenService,
                                           PdlTestdataProperties pdlTestdataProperties,
                                           PdlApiProperties pdlApiProperties,
                                           PdlQ1ApiProperties pdlQ1ApiProperties) {

        var addPdlApiAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(pdlApiProperties)
                        .map(AccessToken::getTokenValue));
        var addPdlQ1ApiAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(pdlQ1ApiProperties)
                        .map(AccessToken::getTokenValue));
        var addPdlTestdataAuthenticationHeaderFilter = AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(() -> tokenService.exchange(pdlTestdataProperties)
                        .map(AccessToken::getTokenValue));

        var addHendelselagerApiKeyAuthenticationHeader = AddAuthenticationRequestGatewayFilterFactory
                .apiKeyAuthenticationHeaderFilter(credentialsHolder.hendelselagerApiKey());
        var addAktoerAdminApiKeyAuthenticationHeader = AddAuthenticationRequestGatewayFilterFactory
                .apiKeyAuthenticationHeaderFilter(credentialsHolder.aktoerAdminApiKey());
        var addElasticSearchBasicAuthenticationHeader = AddAuthenticationRequestGatewayFilterFactory
                .basicAuthAuthenticationHeaderFilter(credentialsHolder.elasticUsername(), credentialsHolder.elasticPassword());

        return builder
                .routes()
                .route(createRoute("pdl-api", pdlApiProperties.getUrl(), addPdlApiAuthenticationHeaderFilter))
                .route(createRoute("pdl-api-q1", pdlQ1ApiProperties.getUrl(), addPdlQ1ApiAuthenticationHeaderFilter))
                .route(createRoute("pdl-testdata", pdlTestdataProperties.getUrl(), addPdlTestdataAuthenticationHeaderFilter))
                .route(createRoute("pdl-identhendelse", "http://pdl-identhendelse-lager.pdl.svc.nais.local", addHendelselagerApiKeyAuthenticationHeader))
                .route(createRoute("pdl-npid", "http://pdl-aktor.pdl.svc.nais.local", addAktoerAdminApiKeyAuthenticationHeader))
                .route(createRoute("pdl-elastic", "https://pdl-es-q.adeo.no", addElasticSearchBasicAuthenticationHeader))
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
