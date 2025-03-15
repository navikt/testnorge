package no.nav.dolly.bestilling.inntektsmelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class OpprettInntektsmeldingCommand implements Callable<Flux<InntektsmeldingResponse>> {

    private final WebClient webClient;
    private final String token;
    private final InntektsmeldingRequest request;
    private final String callId;

    @Override
    public Flux<InntektsmeldingResponse> call() {
        return webClient
                .post()
                .uri("/api/v1/inntektsmelding")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header("Nav-Call-Id", callId)
                .body(BodyInserters.fromPublisher(Mono.just(request), InntektsmeldingRequest.class))
                .retrieve()
                .bodyToFlux(InntektsmeldingResponse.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Flux.just(InntektsmeldingResponse.builder()
                        .fnr(request.getArbeidstakerFnr())
                        .status(WebClientError.describe(error).getStatus())
                        .error(WebClientError.describe(error).getMessage())
                        .miljoe(request.getMiljoe())
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
