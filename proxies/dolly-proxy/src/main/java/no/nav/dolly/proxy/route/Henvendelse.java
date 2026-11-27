package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import no.nav.dolly.proxy.config.Consumers;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Henvendelse {

    private final Consumers consumers;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {

        var bearerAuthenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(consumers.getHenvendelse());

        return spec -> spec
                .path("/henvendelse/**")
                .filters(f -> f.stripPrefix(1)
                        .filter(bearerAuthenticationFilter))
                .uri(consumers.getHenvendelse().getUrl()+"/api");
    }
}