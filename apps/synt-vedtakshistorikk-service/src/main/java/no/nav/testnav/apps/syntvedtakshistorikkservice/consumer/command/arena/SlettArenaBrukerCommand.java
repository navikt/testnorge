package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SlettArenaBrukerCommand implements Callable<Mono<ResponseEntity<JsonNode>>> {

    private final String personident;
    private final String miljoe;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<ResponseEntity<JsonNode>> call() {
        log.info("Sletter ident {} fra Arena Forvalter i miljø {}.", personident, miljoe);
        return webClient
                .delete()
                .uri(builder -> builder
                        .path("/arena/api/v1/bruker")
                        .queryParam("miljoe", miljoe)
                        .queryParam("personident", personident)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(Headers.CALL_ID, Headers.NAV_CALL_ID)
                .header(Headers.CONSUMER_ID, Headers.NAV_CONSUMER_ID)
                .retrieve()
                .toEntity(JsonNode.class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
