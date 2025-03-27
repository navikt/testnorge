package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.NyBruker;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.EIER;

@RequiredArgsConstructor
@Slf4j
public class PostArenaBrukerCommand implements Callable<Mono<NyeBrukereResponse>> {

    private final WebClient webClient;
    private final List<NyBruker> nyeBrukere;
    private final String token;

    private static final ParameterizedTypeReference<Map<String, List<NyBruker>>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<NyeBrukereResponse> call() {
        log.info("Sender inn ny(e) bruker(e) til Arena-forvalteren.");
        return webClient.post()
                .uri(builder -> builder
                        .path("/api/v1/bruker")
                        .queryParam("eier", EIER)
                        .build())
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(
                        Mono.just(Collections.singletonMap("nyeBrukere", nyeBrukere)),
                        REQUEST_TYPE))
                .retrieve()
                .bodyToMono(NyeBrukereResponse.class)
                .doOnError(WebClientError.logTo(log));
    }

}
