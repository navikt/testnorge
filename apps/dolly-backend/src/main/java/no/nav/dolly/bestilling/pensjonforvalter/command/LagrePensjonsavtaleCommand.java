package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonsavtaleRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Slf4j
@RequiredArgsConstructor
public class LagrePensjonsavtaleCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJONSAVTALE_URL = "/api/v2/pensjonsavtale/opprett";

    private final WebClient webClient;
    private final PensjonsavtaleRequest pensjonsavtaleRequest;
    private final String token;

    @Override
    public Flux<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Pensjonsavtale lagre inntekt {}, callId: {}", pensjonsavtaleRequest, callId);
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(PENSJONSAVTALE_URL).build())
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonsavtaleRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(PensjonforvalterResponse
                            .builder()
                            .status(pensjonsavtaleRequest
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
                                                    .path(PENSJONSAVTALE_URL)
                                                    .build())
                                            .build())
                                    .toList())
                            .build());
                });

    }

}
