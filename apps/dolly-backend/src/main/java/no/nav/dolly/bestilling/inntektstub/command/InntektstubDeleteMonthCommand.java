package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.DeleteMonthDTO;
import no.nav.dolly.bestilling.inntektstub.domain.ResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class InntektstubDeleteMonthCommand implements Callable<Mono<ResponseDTO>> {

    private static final String DELETE_INNTEKTER_URL = "/inntektstub/api/v2/inntektsinformasjon";

    private final WebClient webClient;
    private final DeleteMonthDTO deleteMonthDTO;
    private final String token;

    public Mono<ResponseDTO> call() {
        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_INNTEKTER_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(deleteMonthDTO)
                .retrieve()
                .toBodilessEntity()
                .map(entity -> ResponseDTO.builder()
                        .status(HttpStatus.valueOf(entity.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> {
                    var description = WebClientError.describe(error);
                    return Mono.just(ResponseDTO.builder()
                            .status(description.getStatus())
                            .message(description.getMessage())
                            .build());
                });
    }
}