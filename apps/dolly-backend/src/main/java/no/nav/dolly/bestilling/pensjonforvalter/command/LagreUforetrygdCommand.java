package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.RequestTimeout.SHORT_REQUEST_DURATION;

@Slf4j
@RequiredArgsConstructor
public class LagreUforetrygdCommand implements Callable<Mono<PensjonforvalterResponse>> {

    private static final String PENSJON_UT_URL = "/api/v2/vedtak/ut";

    private final WebClient webClient;

    private final String token;

    private final PensjonUforetrygdRequest uforetrygdRequest;

    @Override
    public Mono<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Pensjon lagre uforetrygd {}, callId: {}", uforetrygdRequest, callId);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_UT_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(uforetrygdRequest)
                .retrieve()
                .bodyToMono(PensjonforvalterResponse.class)
                .timeout(Duration.ofSeconds(SHORT_REQUEST_DURATION))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(PensjonforvalterResponse
                            .builder()
                            .status(uforetrygdRequest
                                    .getMiljoer()
                                    .stream()
                                    .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment
                                            .builder()
                                            .miljo(miljoe)
                                            .response(PensjonforvalterResponse.Response
                                                    .builder()
                                                    .httpStatus(PensjonforvalterResponse.HttpStatus
                                                            .builder()
                                                            .status(description.getStatus().value())
                                                            .reasonPhrase(description.getStatus().getReasonPhrase())
                                                            .build())
                                                    .message(description.getMessage()
                                                            .replaceAll("\"timestamp\":\\d+,", ""))
                                                    .path(PENSJON_UT_URL)
                                                    .build())
                                            .build())
                                    .toList())
                            .build());
                });
    }
}