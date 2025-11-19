package no.nav.dolly.proxy.route;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Pensjon {

    private static final String CLUSTER = "dev-gcp";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {
        return build(null);
    }

    Function<PredicateSpec, Buildable<Route>> build(SpecialCase specialCase) {

        // Special case for AFP in Q1.
        if (SpecialCase.AFP_Q1.equals(specialCase)) {
            var uri = targets.getPensjonAfp().formatted("q1");
            var authenticationFilter = authenticationFilterService.getTrygdeetatenAuthenticationFilter(CLUSTER, specialCase.getNamespace(), specialCase.getName(), uri);
            return spec -> spec
                    .path("/pensjon/q1/api/mock-oppsett/**")
                    .filters(f -> f
                            .filter(authenticationFilter)
                            .stripPrefix(1)
                            .rewritePath("/q1/(?<segment>.*)", "/${segment}"))
                    .uri(uri);
        }

        // Special case for AFP in Q2.
        if (SpecialCase.AFP_Q2.equals(specialCase)) {
            var uri = targets.getPensjonAfp().formatted("q2");
            var authenticationFilter = authenticationFilterService.getTrygdeetatenAuthenticationFilter(CLUSTER, specialCase.getNamespace(), specialCase.getName(), uri);
            return spec -> spec
                    .path("/pensjon/q2/api/mock-oppsett/**")
                    .filters(f -> f
                            .filter(authenticationFilter)
                            .stripPrefix(1)
                            .rewritePath("/q2/(?<segment>.*)", "/${segment}"))
                    .uri(uri);
        }

        // Special case for samboerforhold in Q1.
        if (SpecialCase.SAMBOER_Q1.equals(specialCase)) {
            var uri = targets.getPensjonSamboer().formatted("q1");
            var authenticationFilter = authenticationFilterService.getTrygdeetatenAuthenticationFilter(CLUSTER, specialCase.getNamespace(), specialCase.getName(), uri);
            return spec -> spec
                    .path("/pensjon/q1/api/samboer/**")
                    .filters(f -> f
                            .filter(authenticationFilter)
                            .stripPrefix(1)
                            .rewritePath("/q1/(?<segment>.*)", "/${segment}"))
                    .uri(uri);
        }

        // Special case for samboerforhold in Q2.
        if (SpecialCase.SAMBOER_Q2.equals(specialCase)) {
            var uri = targets.getPensjonSamboer().formatted("q2");
            var authenticationFilter = authenticationFilterService.getTrygdeetatenAuthenticationFilter(CLUSTER, specialCase.getNamespace(), specialCase.getName(), uri);
            return spec -> spec
                    .path("/pensjon/q2/api/samboer/**")
                    .filters(f -> f
                            .filter(authenticationFilter)
                            .stripPrefix(1)
                            .rewritePath("/q2/(?<segment>.*)", "/${segment}"))
                    .uri(uri);
        }

        // All other usage.
        return spec -> spec
                .path("/pensjon/api/**")
                .filters(f -> f
                        .stripPrefix(1)
                        .addRequestHeader(HttpHeaders.AUTHORIZATION, "dolly")) // Auth header er required men sjekkes ikke utover det.
                .uri(targets.pensjon);

    }

    @RequiredArgsConstructor
    enum SpecialCase {
        AFP_Q1("pensjon-saksbehandling", "pensjon-afp-offentlig-mock-q1"),
        AFP_Q2("pensjon-saksbehandling", "pensjon-afp-offentlig-mock-q2"),
        SAMBOER_Q1("pensjon-person", "pensjon-samboerforhold-backend-q1"),
        SAMBOER_Q2("pensjon-person", "pensjon-samboerforhold-backend-q2");

        @Getter(AccessLevel.PRIVATE)
        private final String namespace;
        @Getter(AccessLevel.PRIVATE)
        private final String name;

    }

}
