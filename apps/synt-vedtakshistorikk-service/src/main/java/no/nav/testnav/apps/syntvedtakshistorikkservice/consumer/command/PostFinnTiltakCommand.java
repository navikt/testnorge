package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.FinnTiltakRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CONSUMER_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class PostFinnTiltakCommand implements Callable<NyttVedtakResponse> {

    private final String miljoe;
    private final String ident;
    private final WebClient webClient;
    private final FinnTiltakRequest rettighet;

    public PostFinnTiltakCommand(FinnTiltakRequest rettighet, WebClient webClient) {
        this.webClient = webClient;
        this.miljoe = rettighet.getMiljoe();
        this.ident = rettighet.getPersonident();
        this.rettighet = rettighet;
    }

    @Override
    public NyttVedtakResponse call() {
        try {
            log.info("Henter tiltak for ident {} i miljø {}", ident, miljoe);
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/finntiltak")
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(rettighet), FinnTiltakRequest.class))
                    .retrieve()
                    .bodyToMono(NyttVedtakResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente tiltak for ident {} i miljø {}", ident, miljoe, e);
            return null;
        }
    }
}

