package no.nav.testnav.libs.securitycore.texas;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Slf4j
public class TexasClient {

    public static final String ENTRA_ID = "entra_id";
    public static final String TOKENX = "tokenx";

    private static final ParameterizedTypeReference<Map<String, Object>> CLAIMS = new ParameterizedTypeReference<>() {
    };
    private static final Retry RETRY_SERVER_ERRORS = Retry.backoff(2, Duration.ofMillis(200))
            .filter(error -> !(error instanceof WebClientResponseException response && response.getStatusCode().is4xxClientError()))
            .onRetryExhaustedThrow((spec, signal) -> signal.failure());

    private final WebClient webClient;
    private final String tokenEndpoint;
    private final String exchangeEndpoint;
    private final String introspectionEndpoint;

    public TexasClient(String tokenEndpoint, String exchangeEndpoint, String introspectionEndpoint, String localSecret) {
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofSeconds(10))))
                .defaultHeaders(headers -> {
                    if (nonNull(localSecret)) {
                        headers.set(HttpHeaders.AUTHORIZATION, "Dolly " + localSecret);
                    }
                })
                .build();
        this.tokenEndpoint = tokenEndpoint;
        this.exchangeEndpoint = exchangeEndpoint;
        this.introspectionEndpoint = introspectionEndpoint;
    }

    public Mono<AccessToken> token(String identityProvider, String target) {
        return post(tokenEndpoint, target, Map.of("identity_provider", identityProvider, "target", target));
    }

    public Mono<AccessToken> exchange(String identityProvider, String target, String userToken) {
        return post(exchangeEndpoint, target, Map.of("identity_provider", identityProvider, "target", target, "user_token", userToken));
    }

    public Mono<Map<String, Object>> introspect(String identityProvider, String token) {
        return webClient
                .post()
                .uri(introspectionEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("identity_provider", identityProvider, "token", token))
                .retrieve()
                .bodyToMono(CLAIMS)
                .retryWhen(RETRY_SERVER_ERRORS)
                .doOnError(error -> log.error("Texas kunne ikke validere token: {}", error.toString()))
                .flatMap(claims -> Boolean.TRUE.equals(claims.get("active"))
                        ? Mono.just(claims)
                        : Mono.error(new BadJwtException(Objects.toString(claims.get("error"), "Ugyldig token"))));
    }

    private Mono<AccessToken> post(String endpoint, String target, Map<String, String> body) {
        return webClient
                .post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .retryWhen(RETRY_SERVER_ERRORS)
                .doOnError(error -> log.error("Texas kunne ikke hente token for {}: {}", target,
                        error instanceof WebClientResponseException response ? response.getResponseBodyAsString() : error.toString()));
    }
}
