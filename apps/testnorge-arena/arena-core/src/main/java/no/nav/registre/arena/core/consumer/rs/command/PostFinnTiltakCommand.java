package no.nav.registre.arena.core.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyFinnTiltakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.concurrent.Callable;

import reactor.core.publisher.Mono;

import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;


@Slf4j
public class FinnTiltakCommand implements Callable<NyttVedtakResponse> {

    private final String miljoe;
    private final String ident;
    private final WebClient webClient;
    private final RettighetFinnTiltakRequest rettighet;

    public FinnTiltakCommand(RettighetFinnTiltakRequest rettighet, WebClient webClient) {
        this.webClient = webClient;
        this.miljoe = rettighet.getMiljoe();
        this.ident = rettighet.getPersonident();
        this.rettighet = rettighet;
    }

    @Override
    public NyttVedtakResponse call() {
        NyttVedtakResponse response = null;
        try {
            log.info("Henter tiltak for ident {} i miljø {}", ident, miljoe);
            var tiltak = webClient.post()
                    .uri(builder ->
                            builder.path("/arena/syntetiser/tiltaksdeltakelse/finn_tiltak")
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(rettighet.getNyeFinntiltak().get(0)), NyttVedtakTiltak.class))
                    .retrieve()
                    .bodyToMono(NyttVedtakTiltak.class)
                    .block();
            response = new NyttVedtakResponse();
            response.setNyeRettigheterTiltak(Collections.singletonList(tiltak));
            response.setFeiledeRettigheter(Collections.emptyList());
        } catch (Exception e) {
            log.error("Klarte ikke hente tiltak for ident {} i miljø {}", ident, miljoe, e);
        }
        return response;
    }
}

