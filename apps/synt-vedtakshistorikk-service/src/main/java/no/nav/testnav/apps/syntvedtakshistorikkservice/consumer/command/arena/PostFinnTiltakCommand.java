package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.FinnTiltakRequest;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakResponse;
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
public class PostFinnTiltakCommand implements Callable<Mono<NyttVedtakResponse>> {

    private final WebClient webClient;
    private final String token;
    private final FinnTiltakRequest rettighet;

    @Override
    public Mono<NyttVedtakResponse> call() {
        log.info("Henter tiltak for ident {} i miljø {}", rettighet.getPersonident(), rettighet.getMiljoe());
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/finntiltak").build())
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(Mono.just(rettighet), FinnTiltakRequest.class))
                .retrieve()
                .bodyToMono(NyttVedtakResponse.class)
                .doOnError(WebClientError.logTo(log));
    }

}

