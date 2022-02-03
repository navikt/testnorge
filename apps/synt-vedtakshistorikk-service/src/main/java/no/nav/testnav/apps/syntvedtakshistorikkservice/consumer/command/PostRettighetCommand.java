package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@Slf4j
public class PostRettighetCommand implements Callable<NyttVedtakResponse> {

    private final WebClient webClient;
    private final RettighetRequest rettighet;

    public PostRettighetCommand(RettighetRequest rettighet, WebClient webClient) {
        this.webClient = webClient;
        this.rettighet = rettighet;
    }

    @Override
    public NyttVedtakResponse call() {
        try {
            return webClient.post()
                    .uri(builder ->
                            builder.path(rettighet.getArenaForvalterUrlPath())
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromPublisher(Mono.just(rettighet), RettighetRequest.class))
                    .retrieve()
                    .bodyToMono(NyttVedtakResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Kunne ikke opprette rettighet i arena-forvalteren.", e);
            return null;
        }
    }
}
