package no.nav.dolly.proxy.brregstub;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.Texas;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class BrregStubRouteBuilder {

    public static final String BRREGSTUB_CONSUMER = "brreg-stub";

    private final Texas texas;

    @Bean
    public Function<PredicateSpec, Buildable<Route>> buildApiRoute() {
        var url = texas
                .consumer(BRREGSTUB_CONSUMER)
                .orElseThrow(() -> new IllegalStateException("Expected consumer " + BRREGSTUB_CONSUMER + " not found"))
                .getUrl();
        return spec -> spec
                .path("/brreg/**")
                .filters(f -> f.stripPrefix(1))
                .uri(url);
    }

}
