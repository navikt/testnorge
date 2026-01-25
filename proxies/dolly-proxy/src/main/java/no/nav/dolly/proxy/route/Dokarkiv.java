package no.nav.dolly.proxy.route;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Dokarkiv {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "teamdokumenthandtering";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {

        var url = targets.dokarkiv.formatted(env.code);
        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, env.name, url);

        return spec -> spec
                .path("/dokarkiv/api/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(1)
                        .rewritePath("/api/%s/(?<segment>.*)".formatted(env.code), "/rest/journalpostapi/${segment}")
                        .setResponseHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                        .filter(authenticationFilter))
                .uri(url);

    }

    @RequiredArgsConstructor
    public enum SpecialCase {
        Q1("q1", "dokarktiv-q1"),
        Q2("q2", "dokarkiv"),
        Q4("q4", "dokarkiv-q4");

        @Getter
        private final String code;
        private final String name;
    }

}
