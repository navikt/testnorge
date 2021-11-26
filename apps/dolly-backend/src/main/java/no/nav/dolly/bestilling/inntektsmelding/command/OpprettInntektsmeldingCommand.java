package no.nav.dolly.bestilling.inntektsmelding.command;

import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

public record OpprettInntektsmeldingCommand(WebClient webClient,
                                            String token,
                                            InntektsmeldingRequest request,
                                            String callId) implements Callable<Mono<ResponseEntity<InntektsmeldingResponse>>> {
    @Override
    public Mono<ResponseEntity<InntektsmeldingResponse>> call() {
        return webClient.post()
                .uri("/api/v1/inntektsmelding")
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header("Nav-Call-Id", callId)
                .body(BodyInserters.fromPublisher(Mono.just(request), InntektsmeldingRequest.class))
                .retrieve()
                .toEntity(InntektsmeldingResponse.class);
    }
}
