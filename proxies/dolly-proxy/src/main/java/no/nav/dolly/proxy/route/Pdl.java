package no.nav.dolly.proxy.route;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import no.nav.dolly.proxy.auth.PdlAuthConfig;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Pdl {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "pdl";

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;
    private final PdlAuthConfig pdlAuthConfig;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull Pdl.SpecialCase env) {

        var url = switch (env) {
            case API -> targets.pdlApi;
            case API_Q1 -> targets.pdlApiQ1;
            case ELASTIC -> targets.pdlElastic;
            case IDENTHENDELSE -> targets.pdlIdenthendelse;
            case TESTDATA -> targets.pdlTestdata;
        };
        var bearerAuthenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, env.name, url);
        var basicAuthenticationFilter = authenticationFilterService
                .getBasicAuthenticationFilter(pdlAuthConfig.getElasticUsername(), pdlAuthConfig.getElasticPassword());
        var apiKeyAuthenticationFilter = authenticationFilterService
                .getApiKeyAuthenticationFilter(pdlAuthConfig.getHendelseApiKey());

        return switch (env) {

            case API -> spec -> spec
                    .path("/pdl-api/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(bearerAuthenticationFilter))
                    .uri(url);

            case API_Q1 -> spec -> spec
                    .path("/pdl-api-q1/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(bearerAuthenticationFilter))
                    .uri(url);

            case ELASTIC -> spec -> spec
                    .path("/pdl-elastic/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(basicAuthenticationFilter))
                    .uri(url);

            case IDENTHENDELSE -> spec -> spec
                    .path("/pdl-identhendelse/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(apiKeyAuthenticationFilter))
                    .uri(url);

            case TESTDATA -> spec -> spec
                    .path("/pdl-testdata/**")
                    .filters(f -> f
                            .stripPrefix(1)
                            .filter(bearerAuthenticationFilter))
                    .uri(url);


        };

    }

    @RequiredArgsConstructor
    enum SpecialCase {
        API("pdl-api"),
        API_Q1("pdl-api-q1"),
        ELASTIC("pdl-elastic"),
        IDENTHENDELSE("pdl-identhendelse"),
        TESTDATA("pdl-testdata");

        @Getter(AccessLevel.PACKAGE)
        private final String name;
    }

}
