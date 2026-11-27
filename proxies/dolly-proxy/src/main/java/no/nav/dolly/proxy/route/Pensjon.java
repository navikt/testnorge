package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import no.nav.dolly.proxy.config.Consumers;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Pensjon {

    private final Consumers consumers;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build() {
        return build(null);
    }

    Function<PredicateSpec, Buildable<Route>> build(SpecialCase env) {

        // Special case for AFP in Q1.
        if (SpecialCase.AFP_Q1.equals(env)) {
            var serverProperties = consumers.getPensjonAfpQ1();
            var uri = serverProperties.getUrl();
            var authenticationFilter = authenticationFilterService
                    .getTrygdeetatenAuthenticationFilter(serverProperties);
            return spec -> spec
                    .path("/pensjon/q1/api/mock-oppsett/**")
                    .filters(f -> f
                            .filter(authenticationFilter)
                            .stripPrefix(1)
                            .rewritePath("/q1/(?<segment>.*)", "/${segment}"))
                    .uri(uri);
        }

        // Special case for AFP in Q2.
        if (SpecialCase.AFP_Q2.equals(env)) {
            var serverProperties = consumers.getPensjonAfpQ2();
            var uri = serverProperties.getUrl();
            var authenticationFilter = authenticationFilterService
                    .getTrygdeetatenAuthenticationFilter(serverProperties);
            return spec -> spec
                    .path("/pensjon/q2/api/mock-oppsett/**")
                    .filters(f -> f
                            .filter(authenticationFilter)
                            .stripPrefix(1)
                            .rewritePath("/q2/(?<segment>.*)", "/${segment}"))
                    .uri(uri);
        }

        // Special case for samboerforhold in Q1.
        if (SpecialCase.SAMBOER_Q1.equals(env)) {
            var serverProperties = consumers.getPensjonSamboerQ1();
            var uri = serverProperties.getUrl();
            var authenticationFilter = authenticationFilterService
                    .getTrygdeetatenAuthenticationFilter(serverProperties);
            return spec -> spec
                    .path("/pensjon/q1/api/samboer/**")
                    .filters(f -> f
                            .filter(authenticationFilter)
                            .stripPrefix(1)
                            .rewritePath("/q1/(?<segment>.*)", "/${segment}"))
                    .uri(uri);
        }

        // Special case for samboerforhold in Q2.
        if (SpecialCase.SAMBOER_Q2.equals(env)) {
            var serverProperties = consumers.getPensjonSamboerQ2();
            var uri = serverProperties.getUrl();
            var authenticationFilter = authenticationFilterService
                    .getTrygdeetatenAuthenticationFilter(serverProperties);
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
                .uri(consumers.getPensjon().getUrl());

    }

    enum SpecialCase {
        AFP_Q1,
        AFP_Q2,
        SAMBOER_Q1,
        SAMBOER_Q2
    }

}
