package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.WebClientFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;

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

        return webClient.delete()
                .uri(builder ->
                        builder.path("/api/v1/bruker")
                                .queryParam("miljoe", miljoe)
                                .queryParam("personident", personident)
                                .build()
                )
                .header(AUTHORIZATION, "Bearer " + token)
                .header(Headers.CALL_ID, Headers.NAV_CALL_ID)
                .header(Headers.CONSUMER_ID, Headers.NAV_CONSUMER_ID)
                .retrieve()
                .toEntity(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
