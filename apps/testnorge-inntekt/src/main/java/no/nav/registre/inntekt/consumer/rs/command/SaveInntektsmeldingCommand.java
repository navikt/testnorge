package no.nav.registre.inntekt.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.response.InntektsmeldingResponse;

@Slf4j
@RequiredArgsConstructor
public class SaveInntektsmeldingCommand implements Callable<Mono<InntektsmeldingResponse>> {
    private final WebClient webClient;
    private final InntektsmeldingRequest request;
    private final String token;
    private final String navCallId;


    @Override
    public Mono<InntektsmeldingResponse> call(){
        return webClient
                .post()
                .uri("/api/v1/inntektsmelding")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Nav-Call-Id",  navCallId)
                .body(BodyInserters.fromPublisher(Mono.just(request), InntektsmeldingRequest.class))
                .retrieve()
                .bodyToMono(InntektsmeldingResponse.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException webClientResponseException) {
                        log.error(
                                "Feil ved lagring av inntektsmelding med body: \n{}.",
                                webClientResponseException.getResponseBodyAsString(),
                                error
                        );
                    } else {
                        log.error("Feil ved lagring av inntektsmelding.", error);
                    }
                });
    }
}
