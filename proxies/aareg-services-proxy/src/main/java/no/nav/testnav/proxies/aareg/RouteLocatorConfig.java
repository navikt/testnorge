package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(SecurityConfig.class)
@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, TrygdeetatenAzureAdTokenService tokenService, AaregServicesProperties properties) {

        var authenticationFilter = AddAuthenticationRequestGatewayFilterFactory
            .bearerAuthenticationHeaderFilter(
                () -> tokenService
                    .exchange(properties)
                    .map(AccessToken::getTokenValue)
            );

        return builder
            .routes()
            // TODO: Resolve w/auth.
            /*.route(spec -> spec.path("/api/v1/**").uri(properties.getUrl()))
            .route(spec -> spec.path("/api/v2/**")
                .filters(filterSpec -> filterSpec.filter(addAuthenticationHeaderDevFilter))
                .uri(properties.getUrl()))*/
            .build();
    }

    @Configuration
    @ConfigurationProperties(prefix = "aareg")
    public static class AaregServicesProperties extends ServerProperties {
    }

}
