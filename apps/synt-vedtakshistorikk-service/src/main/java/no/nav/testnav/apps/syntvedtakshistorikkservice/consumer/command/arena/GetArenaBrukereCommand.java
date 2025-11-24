package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@RequiredArgsConstructor
@Slf4j
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
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(NyeBrukereResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(WebClientError.logTo(log));
    }

}
