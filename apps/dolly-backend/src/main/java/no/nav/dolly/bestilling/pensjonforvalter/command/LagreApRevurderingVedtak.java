package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.RevurderingVedtakRequest;
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
public class LagreApRevurderingVedtak implements Callable<Mono<PensjonforvalterResponse>> {

    private static final String PENSJON_AP_REVURDERING_VEDTAK_URL = "/api/v1/ap/revurdering-vedtak";

    private final WebClient webClient;
    private final RevurderingVedtakRequest revurderingVedtakRequest;
    private final String token;

    @Override
    public Mono<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("PEN sende ap/revurdering-vedtak {}, callId: {}", revurderingVedtakRequest, callId);

        return webClient
                .post()
                .uri(path -> path
                        .path(PENSJON_AP_REVURDERING_VEDTAK_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(revurderingVedtakRequest)
                .retrieve()
                .bodyToMono(PensjonforvalterResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(PensjonforvalterResponse
                            .builder()
                            .status(revurderingVedtakRequest
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
                                                    .path(PENSJON_AP_REVURDERING_VEDTAK_URL)
                                                    .build())
                                            .build())
                                    .toList())
                            .build());
                });
    }
}
