package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import lombok.AllArgsConstructor;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CONSUMER_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CONSUMER_ID;

@AllArgsConstructor
public class GetArenaBrukereCommand implements Callable<Mono<NyeBrukereResponse>> {

    private final MultiValueMap<String, String> queryParams;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<NyeBrukereResponse> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/bruker")
                                .queryParams(queryParams)
                                .build()
                )
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(NyeBrukereResponse.class);
    }
}
