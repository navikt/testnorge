package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Aareg {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "arbeidsforhold";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env, boolean writeable) {

        var name = getName(env, writeable);
        var url = getUrl(env, writeable);
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

    String getName(SpecialCase env, boolean writeable) {
        return writeable ?
                env.writeableName :
                env.readableName;
    }

    String getUrl(SpecialCase env, boolean writeable) {
        return writeable ?
                targets.aaregVedlikehold.formatted(env.writeableHost) :
                targets.aaregServices.formatted(env.readableHost);
    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1", "aareg-services-nais-q1", "aareg-services-q1", "aareg-dolly-api-q1", "aareg-dolly-api-q1"),
        Q2("q2", "aareg-services-nais", "aareg-services", "aareg-dolly-api-q2", "aareg-dolly-api-q2"),
        Q4("q4", "aareg-services-nais-q4", "aareg-services-q4", "aareg-dolly-api-q4", "aareg-dolly-api-q4");

        private final String code;
        private final String readableName;
        private final String readableHost;
        private final String writeableName;
        private final String writeableHost;
    }

}
