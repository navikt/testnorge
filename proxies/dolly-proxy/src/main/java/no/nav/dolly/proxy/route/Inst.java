package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactiveproxy.filter.AddAuthenticationRequestGatewayFilterFactory;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Inst {

    private static final String CLUSTER = "dev-fss";
    private static final String NAME = "opphold-testdata";
    private static final String NAMESPACE = "team-rocket";

    private final Targets targets;
    private final AzureTrygdeetatenTokenService trygdeetatenTokenService;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/inst/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .filter(getAuthenticationFilter()))
                .uri(targets.inst);
    }

    private GatewayFilter getAuthenticationFilter() {
        var serverProperties = ServerProperties.of(CLUSTER, NAMESPACE, NAME, targets.inst);
        return AddAuthenticationRequestGatewayFilterFactory
                .bearerAuthenticationHeaderFilter(
                        () -> trygdeetatenTokenService
                                .exchange(serverProperties)
                                .map(AccessToken::getTokenValue));
    }

}
