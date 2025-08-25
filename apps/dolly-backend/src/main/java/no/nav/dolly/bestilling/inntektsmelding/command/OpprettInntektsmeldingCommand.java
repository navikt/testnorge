package no.nav.dolly.bestilling.inntektsmelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class OpprettInntektsmeldingCommand implements Callable<Mono<InntektsmeldingResponse>> {

    private final WebClient webClient;
    private final String token;
    private final InntektsmeldingRequest request;
    private final String callId;

    @Override
    public Mono<InntektsmeldingResponse> call() {
        return webClient
                .post()
                .uri("/api/v1/inntektsmelding")
                .headers(WebClientHeader.bearer(token))
                .header("Nav-Call-Id", callId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InntektsmeldingResponse.class)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable ->
                        InntektsmeldingResponse.of(WebClientError.describe(throwable), request.getArbeidstakerFnr(), request.getMiljoe()))
                .retryWhen(WebClientError.is5xxException());
    }
}
