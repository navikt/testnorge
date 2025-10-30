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
public class Kontoregister {

    private static final String CLUSTER = "dev-gcp";
    private static final String NAMESPACE = "okonomi";
    private static final String NAME = "sokos-kontoregister-person";

    private final Targets targets;
    private final FakedingsService fakedingsService;
    private final TokenXService tokenXService;

    Function<PredicateSpec, Buildable<Route>> build() {

        return spec -> spec
                .path("/kontoregister/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .filter(getAuthenticationFilter()))
                .uri(targets.kontoregister);
    }

    private GatewayFilter getAuthenticationFilter() {
        var serverProperties = ServerProperties.of(CLUSTER, NAMESPACE, NAME, targets.fullmakt);
        return fakedingsService
                .bearerAuthenticationHeaderFilter(fakedingsService, tokenXService, serverProperties);
    }

}
