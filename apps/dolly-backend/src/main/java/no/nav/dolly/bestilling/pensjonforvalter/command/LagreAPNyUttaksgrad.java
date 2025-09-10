package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonNyUtaksgradRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Slf4j
@RequiredArgsConstructor
public class LagreAPNyUttaksgrad implements Callable<Mono<PensjonforvalterResponse>> {

    private static final String ALDERSPENSJON_NY_UTTAKSGRAD_URL = "/api/v1/ap/ny-uttaksgrad";

    private final WebClient webClient;
    private final AlderspensjonNyUtaksgradRequest nyUttaksgradRequest;
    private final String token;

    @Override
    public Mono<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("PEN sende ap/ny-uttaksgrad {}, callId: {}", nyUttaksgradRequest, callId);

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ALDERSPENSJON_NY_UTTAKSGRAD_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(nyUttaksgradRequest)
                .retrieve()
                .bodyToMono(PensjonforvalterResponse.class)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(PensjonforvalterResponse
                            .builder()
                            .status(nyUttaksgradRequest
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
                                                    .message(description.getMessage())
                                                    .path(ALDERSPENSJON_NY_UTTAKSGRAD_URL)
                                                    .build())
                                            .build())
                                    .toList())
                            .build());
                });
    }
}
