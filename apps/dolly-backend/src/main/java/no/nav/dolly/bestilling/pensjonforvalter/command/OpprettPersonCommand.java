package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class OpprettPersonCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_OPPRETT_PERSON_URL = "/api/v1/person";

    private final WebClient webClient;
    private final PensjonPersonRequest pensjonPersonRequest;
    private final String token;

    @Override
    public Flux<PensjonforvalterResponse> call() {
        var callId = generateCallId();
        log.info("Pensjon opprett person {}, callId: {}", pensjonPersonRequest, callId);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_OPPRETT_PERSON_URL)
                        .build())
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonPersonRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(pensjonPersonRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .status(WebClientError.describe(error).getStatus().value())
                                                                .reasonPhrase(WebClientError.describe(error).getStatus().getReasonPhrase())
                                                                .build())
                                                        .message(WebClientError.describe(error).getMessage())
                                                        .path(PENSJON_OPPRETT_PERSON_URL)
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }

}
