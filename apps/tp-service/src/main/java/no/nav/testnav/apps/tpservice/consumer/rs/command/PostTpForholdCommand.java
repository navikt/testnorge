package no.nav.testnav.apps.tpservice.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpservice.consumer.rs.request.LagreTpForholdRequest;
import no.nav.testnav.apps.tpservice.consumer.rs.response.PensjonforvalterResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class PostTpForholdCommand implements Callable<Mono<PensjonforvalterResponse>> {

    private final WebClient webClient;
    private final String token;
    private final LagreTpForholdRequest lagreTpForholdRequest;

    public Mono<PensjonforvalterResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/tp/forhold")
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
//                .header(HEADER_NAV_CALL_ID, generateCallId())
//                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpForholdRequest)
                .retrieve()
                .bodyToMono(PensjonforvalterResponse.class);
    }
}
