package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonsavtaleRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonsavtaleRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(pensjonsavtaleRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .status(WebClientFilter.getStatus(error).value())
                                                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                                                .build())
                                                        .message(WebClientFilter.getMessage(error))
                                                        .path(PENSJONSAVTALE_URL)
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }

}
