package no.nav.dolly.libs.texas;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class Texas {

    private static final String JSON_TOKEN_REQUEST = StringUtils.trimAllWhitespace("""
            {
                "identity_provider": "azuread",
                "target": "%s"
            }
            """);
    private static final String JSON_EXCHANGE_REQUEST = StringUtils.trimAllWhitespace("""
            {
                "identity_provider": "azuread",
                "target": "%s",
                "user_token": "%s"
            }
            """);
    private static final String JSON_INTROSPECTION_REQUEST = StringUtils.trimAllWhitespace("""
            {
                "identity_provider": "azuread",
                "token": "%s"
            }
            """);

    private final WebClient webClient;
    private final String tokenUrl;
    private final String exchangeUrl;
    private final String introspectUrl;
    private final TexasConsumers consumers;
    private final ConcurrentHashMap<String, WebClient> webClientsForConsumers = new ConcurrentHashMap<>();
    private final TexasTokenCache tokenCache = new TexasTokenCache();

    public Mono<TexasToken> get(String audience)
            throws TexasException {
        return tokenCache
                .get(audience, aud -> webClient
                        .post()
                        .uri(URI.create(tokenUrl))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(JSON_TOKEN_REQUEST.formatted(audience))
                        .retrieve()
                        .bodyToMono(TexasToken.class)
                        .retryWhen(WebClientError.is5xxException())
                        .doOnError(WebClientError.logTo(log)))
                .onErrorMap(error -> new TexasException("Failed to get token using audience '%s'".formatted(audience), error));
    }

    public Mono<TexasToken> getToken(String consumerName) {
        return get(consumer(consumerName).getAudience());
    }

    public Mono<TexasToken> exchange(String audience, String token)
            throws TexasException {
        return webClient
                .post()
                .uri(URI.create(exchangeUrl))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON_EXCHANGE_REQUEST.formatted(audience, token))
                .retrieve()
                .bodyToMono(TexasToken.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorMap(error -> new TexasException("Failed to exchange token using audience '%s'".formatted(audience), error));
    }

    public Mono<TexasToken> exchangeToken(String consumerName, String token) {
        return exchange(consumer(consumerName).getAudience(), token);
    }

    public Mono<String> introspect(String token)
            throws TexasException {
        return webClient
                .post()
                .uri(introspectUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON_INTROSPECTION_REQUEST.formatted(token))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorMap(error -> new TexasException("Failed to introspect token", error));
    }

    public TexasConsumer consumer(String name)
            throws TexasException {
        return consumers
                .get(name)
                .orElseThrow(() -> new TexasException("Consumer '%s' not found".formatted(name)));
    }

    /**
     * Convenience method to set the Authorization header with a Bearer token using token for a given consumer.
     *
     * @param consumerName The consumer name to use for the token, as for calls to {@link #getToken(String)}.
     * @return A {@link Consumer} that sets the Authorization header with the Bearer token.
     * @throws TexasException If the consumer is not found, or if no token could be retrieved.
     */
    public Consumer<HttpHeaders> bearer(String consumerName)
            throws TexasException {
        return headers -> getToken(consumerName)
                .map(t -> "Bearer " + t.access_token())
                .subscribe(s -> headers.set(HttpHeaders.AUTHORIZATION, s));
    }

    /**
     * Convenience method to get a {@link WebClient} for a given consumer.
     *
     * @param consumerName The consumer name.
     * @return A {@link WebClient} for the given consumer.
     * @throws TexasException If the consumer is not found or has no configured URL.
     */
    public WebClient webClient(String consumerName)
            throws TexasException {
        return webClientsForConsumers.computeIfAbsent(
                consumerName,
                name -> {
                    var consumer = consumers
                            .get(name)
                            .orElseThrow(() -> new TexasException("Consumer '%s' not found".formatted(name)));
                    var url = consumer.getUrl();
                    if (!StringUtils.hasText(url)) {
                        throw new TexasException("Consumer '%s' has no URL".formatted(name));
                    }
                    return webClient
                            .mutate()
                            .baseUrl(consumer.getUrl())
                            .build();
                }
        );
    }

}
