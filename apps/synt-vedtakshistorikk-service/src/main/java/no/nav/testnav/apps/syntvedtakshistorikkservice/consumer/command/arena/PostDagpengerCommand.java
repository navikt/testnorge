package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerRequestDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@RequiredArgsConstructor
@Slf4j
public class PostDagpengerCommand implements Callable<Mono<DagpengerResponseDTO>> {

    private final DagpengerRequestDTO request;
    private final String path;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<DagpengerResponseDTO> call() {
        return webClient.post()
                .uri(builder -> builder.path(path).build())
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(Mono.just(request), DagpengerRequestDTO.class))
                .retrieve()
                .bodyToMono(DagpengerResponseDTO.class)
                .doOnError(WebClientError.logTo(log));
    }

}
