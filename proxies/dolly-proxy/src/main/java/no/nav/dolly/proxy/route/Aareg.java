package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import no.nav.dolly.proxy.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
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

    private final Consumers consumers;
    private final AuthenticationFilterService authenticationFilterService;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env, boolean writeable) {

        var serverProperties = getServerProperties(env, writeable);
        var url = serverProperties.getUrl();
        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(serverProperties);

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

    ServerProperties getServerProperties(SpecialCase env, boolean writeable) {
        if (writeable) {
            return switch (env) {
                case Q1 -> consumers.getAaregVedlikeholdQ1();
                case Q2 -> consumers.getAaregVedlikeholdQ2();
                case Q4 -> consumers.getAaregVedlikeholdQ4();
            };
        }
        return switch (env) {
            case Q1 -> consumers.getAaregServicesQ1();
            case Q2 -> consumers.getAaregServicesQ2();
            case Q4 -> consumers.getAaregServicesQ4();
        };
    }

    @RequiredArgsConstructor
    enum SpecialCase {
        Q1("q1"),
        Q2("q2"),
        Q4("q4");

        private final String code;
    }

}
