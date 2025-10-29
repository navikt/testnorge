package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.FakedingsService;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Fullmakt {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "repr";
    private static final String NAME = "repr-fullmakt";

    private final Targets targets;
    private final FakedingsService fakedingsService;
    private final TokenXService tokenXService;

    Function<PredicateSpec, Buildable<Route>> build() {
        return spec -> spec
                .path("/fullmakt/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .setResponseHeader("Content-Type", "application/json; charset=UTF-8")
                        .filter(getAuthenticationFilter()))
                .uri(targets.fullmakt);
    }

    private GatewayFilter getAuthenticationFilter() {
        var serverProperties = ServerProperties.of(CLUSTER, NAMESPACE, NAME, targets.fullmakt);
        return fakedingsService
                .bearerAuthenticationHeaderFilter(fakedingsService, tokenXService, serverProperties);
    }

}
