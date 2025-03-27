package no.nav.dolly.budpro.texas;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class TexasService {

    private static final String JSON_TOKEN_REQUEST = StringUtils.trimAllWhitespace("""
            {
                "identity_provider": "azuread",
                "target": "%s"
            }
            """);

    private final WebClient webClient;
    private final String tokenUrl;
    private final String exchangeUrl;
    private final String introspectUrl;
    private final TexasConsumers consumers;

    public Mono<TexasToken> getToken(String name)
            throws TexasException {
        return webClient
                .post()
                .uri(URI.create(tokenUrl))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON_TOKEN_REQUEST.formatted(getConsumer(name).getAudience()))
                .retrieve()
                .bodyToMono(TexasToken.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

    public TexasConsumer getConsumer(String name)
            throws TexasException {
        return consumers.get(name).orElseThrow(() -> new TexasException("Consumer '%s' not found".formatted(name)));
    }

}
