package no.nav.registre.arena.core.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class GetArenaBrukereCommand implements Callable<NyeBrukereResponse> {

    private final WebClient webClient;
    private final MultiValueMap<String, String> queryParams;

    public GetArenaBrukereCommand(MultiValueMap<String, String> queryParams, WebClient webClient) {
        this.webClient = webClient;
        this.queryParams = queryParams;
    }

    @Override
    public NyeBrukereResponse call() {
        NyeBrukereResponse response = null;
        try {
            response = webClient.get()
                    .uri(builder ->
                            builder.path("/v1/bruker")
                                    .queryParams(queryParams)
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .retrieve()
                    .bodyToMono(NyeBrukereResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å hente arbeidssoekere fra Arena-forvalteren.", e);
        }
        return response;
    }
}
