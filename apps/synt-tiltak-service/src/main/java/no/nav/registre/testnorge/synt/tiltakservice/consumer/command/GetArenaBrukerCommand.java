package no.nav.registre.testnorge.synt.tiltakservice.consumer.command;

import static no.nav.registre.testnorge.domain.dto.arena.testnorge.request.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.domain.dto.arena.testnorge.request.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.domain.dto.arena.testnorge.request.util.Headers.NAV_CALL_ID;
import static no.nav.registre.testnorge.domain.dto.arena.testnorge.request.util.Headers.NAV_CONSUMER_ID;

import java.util.concurrent.Callable;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;

@Slf4j
public class GetArenaBrukerCommand implements Callable<NyeBrukereResponse> {

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String eier;

    public GetArenaBrukerCommand(String ident, String miljoe, String eier, WebClient webClient) {
        this.webClient = webClient;
        this.eier = eier;
        this.ident = ident;
        this.miljoe = miljoe;
    }

    @Override
    public NyeBrukereResponse call() {
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/v1/bruker")
                                    .queryParam("filter-personident", ident)
                                    .queryParam("filter-miljoe", miljoe)
                                    .queryParam("filter-eier", eier)
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .retrieve()
                    .bodyToMono(NyeBrukereResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å hente arbeidssoeker fra Arena-forvalteren.", e);
            return null;
        }
    }
}
