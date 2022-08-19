package no.nav.testnav.apps.tpservice.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpservice.consumer.rs.request.LagreTpYtelseRequest;
import no.nav.testnav.apps.tpservice.consumer.rs.response.PensjonforvalterResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.tpservice.util.CallIdUtil.generateCallId;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.CONSUMER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@RequiredArgsConstructor
public class PostTpYtelseCommand implements Callable<Mono<PensjonforvalterResponse>> {
    private final WebClient webClient;
    private final String token;
    private final LagreTpYtelseRequest lagreTpYtelseRequest;

    public Mono<PensjonforvalterResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/tp/ytelse")
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpYtelseRequest)
                .retrieve()
                .bodyToMono(PensjonforvalterResponse.class)
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    return Mono.empty();
                });
    }
}
