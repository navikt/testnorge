package no.nav.dolly.bestilling.inntektsmelding.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class OpprettInntektsmeldingCommand implements Callable<Flux<InntektsmeldingResponse>> {

    private final WebClient webClient;
    private final String token;
    private final InntektsmeldingRequest request;
    private final String callId;

    @Override
    public Flux<InntektsmeldingResponse> call() {

        return webClient.post()
                .uri("/api/v1/inntektsmelding")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header("Nav-Call-Id", callId)
                .body(BodyInserters.fromPublisher(Mono.just(request), InntektsmeldingRequest.class))
                .retrieve()
                .bodyToFlux(InntektsmeldingResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Flux.just(InntektsmeldingResponse.builder()
                        .fnr(request.getArbeidstakerFnr())
                        .error(WebClientFilter.getMessage(error))
                        .miljoe(request.getMiljoe())
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
