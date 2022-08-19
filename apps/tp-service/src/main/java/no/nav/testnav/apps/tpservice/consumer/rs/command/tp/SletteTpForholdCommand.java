package no.nav.testnav.apps.tpservice.consumer.rs.command.tp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpservice.consumer.rs.response.PensjonforvalterResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.tpservice.util.CallIdUtil.generateCallId;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.CONSUMER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@RequiredArgsConstructor
public class SletteTpForholdCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private final WebClient webClient;
    private final String ident;
    private final Set<String> miljoer;
    private final String token;


    public Flux<PensjonforvalterResponse> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/tp/person/forhold")
                        .queryParam("miljoer", String.join(",", miljoer))
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("pid", ident)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    return Mono.empty();
                });
    }
}
