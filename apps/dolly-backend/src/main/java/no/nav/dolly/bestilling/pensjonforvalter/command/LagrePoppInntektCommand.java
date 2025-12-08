package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Slf4j
@RequiredArgsConstructor
public class LagrePoppInntektCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String POPP_INNTEKT_URL = "/pensjon/api/v1/inntekt";

    private final WebClient webClient;
    private final String token;
    private final PensjonPoppInntektRequest pensjonPoppInntektRequest;

    @Override
    public Flux<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Popp lagre inntekt {}, callId: {}", pensjonPoppInntektRequest, callId);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(POPP_INNTEKT_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonPoppInntektRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(PensjonforvalterResponse
                            .builder()
                            .status(pensjonPoppInntektRequest
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
                                                    .path(POPP_INNTEKT_URL)
                                                    .build())
                                            .build())
                                    .toList())
                            .build());
                });

    }

}
