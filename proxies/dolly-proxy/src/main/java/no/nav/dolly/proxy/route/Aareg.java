package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Aareg {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "arbeidsforhold";
    private static final String NAME_READABLE = "aareg-services-nais-%s"; // Note replacement pattern.
    private static final String NAME_WRITEABLE = "aareg-dolly-api-%s"; // Note replacement pattern.

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(SpecialCase env, boolean writeable) {

        var name = writeable ?
                NAME_WRITEABLE.formatted(env.code) :
                NAME_READABLE.formatted(env.code);
        var url = writeable ?
                targets.aaregVedlikehold.formatted(env.code) :
                targets.aaregServices.formatted(env.code);

        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, name, url);

        if (writeable) {
            return spec -> spec
                    .path("/aareg/%s/**".formatted(env.code))
                    .and()
                    .method(HttpMethod.POST, HttpMethod.PUT)
                    .filters(f -> f
                            .stripPrefix(2)
                            .filter(authenticationFilter)
                    )
                    .uri(url);
        } else {
            return spec -> spec
                    .path("/aareg/%s/**".formatted(env.code))
                    .and()
                    .method(HttpMethod.GET)
                    .filters(f -> f
                            .stripPrefix(2)
                            .filter(authenticationFilter)
                    )
                    .uri(url);
        }

    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2"),
        Q4("q4");

        private final String code;
    }

}
