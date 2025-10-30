package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AzureService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Skjermingsregister {

    private static final String CLUSTER = "dev-fss";
    private static final String NAME = "skjermede-personer";
    private static final String NAMESPACE = "nom";

    private final Targets targets;
    private final AzureService azureService;

    Function<PredicateSpec, Buildable<Route>> build() {
        var authenticationFilter = azureService.getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, NAME, targets.skjermingsregister);
        return spec -> spec
                .path("/skjermingsregister/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .filter(authenticationFilter))
                .uri(targets.skjermingsregister);
    }

}
