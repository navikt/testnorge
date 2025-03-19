package no.nav.dolly.budpro.texas;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class TexasService {

    private static final String JSON_TOKEN_REQUEST = """
            {
                "identity_provider": "azuread",
                "target": "%s"
            }
            """.replaceAll("\\s+", "");

    private final WebClient webClient;

    @PostConstruct
    void postConstruct() {
        var token = this.getToken("audience").block();
        log.info("Token: {}", token);
    }

    /**
     * Get token from Texas.
     * @param audience On the format {@code api://<cluster>.<namespace>.<other-api-app-name>/.default}.
     * @return A token.
     */
    public Mono<TexasToken> getToken(String audience) {
        return webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON_TOKEN_REQUEST.formatted(audience))
                .retrieve()
                .bodyToMono(TexasToken.class)
                .retryWhen(Retry
                        .backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }

}
