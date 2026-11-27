package no.nav.dolly.proxy.route;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import no.nav.dolly.proxy.auth.PdlAuthConfig;
import no.nav.dolly.proxy.config.Consumers;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class Pdl {

    private final Consumers consumers;
    private final AuthenticationFilterService authenticationFilterService;
    private final PdlAuthConfig pdlAuthConfig;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull Pdl.SpecialCase env) {

        var serverProperties = switch (env) {
            case API -> consumers.getPdlApiQ2();
            case API_Q1 -> consumers.getPdlApiQ1();
            case IDENTHENDELSE -> consumers.getPdlIdenthendelse();
            case TESTDATA -> consumers.getPdlTestdata();
        };
        var url = serverProperties.getUrl();
        var bearerAuthenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(serverProperties);
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

    enum SpecialCase {
        API,
        API_Q1,
        IDENTHENDELSE,
        TESTDATA
    }

}
