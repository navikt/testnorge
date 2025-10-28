package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;
import no.nav.testnav.libs.securitycore.domain.AccessToken;

import java.util.function.Function;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Component
@RequiredArgsConstructor
class Udistub {

    private static final String CLUSTER = "dev-fss";
    private static final String NAME = "testnav-udi-stub";
    private static final String NAMESPACE = "dolly";

    private final Targets targets;
    private final TokenExchange tokenExchange;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/udistub/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .setResponseHeader(CONTENT_TYPE, "application/json; charset=UTF-8")
                        .filter(getAuthenticationFilter()))
                .uri(targets.udistub);
    }

    private GatewayFilter getAuthenticationFilter() {
        var serverProperties = ServerProperties.of(CLUSTER, NAME, NAMESPACE, targets.udistub);
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> tokenExchange
                                .exchange(serverProperties)
                                .map(AccessToken::getTokenValue));
    }

}
