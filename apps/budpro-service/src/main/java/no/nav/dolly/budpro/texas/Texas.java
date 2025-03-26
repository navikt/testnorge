package no.nav.dolly.budpro.texas;

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

    public Mono<TexasToken> get(String audience) {
        return webClient
                .post()
                .uri(URI.create(tokenUrl))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON_TOKEN_REQUEST.formatted(audience))
                .retrieve()
                .bodyToMono(TexasToken.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

    public Mono<TexasToken> getToken(String consumerName) {
        return get(consumer(consumerName).getAudience());
    }

    public Mono<TexasToken> exchange(String audience, String token) {
        return webClient
                .post()
                .uri(URI.create(exchangeUrl))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON_EXCHANGE_REQUEST.formatted(audience, token))
                .retrieve()
                .bodyToMono(TexasToken.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

    public Mono<TexasToken> exchangeToken(String consumerName, String token) {
        return exchange(consumer(consumerName).getAudience(), token);
    }

    public Mono<String> introspect(String token) {
        return webClient
                .post()
                .uri(introspectUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON_INTROSPECTION_REQUEST.formatted(token))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

    public TexasConsumer consumer(String name)
            throws TexasException {
        return consumers
                .get(name)
                .orElseThrow(() -> new TexasException("Consumer '%s' not found".formatted(name)));
    }

    public static Consumer<HttpHeaders> bearer(Mono<TexasToken> token) {
        return headers -> token
                .map(t -> "Bearer " + t.access_token())
                .subscribe(s -> headers.set(HttpHeaders.AUTHORIZATION, s));
    }

}
