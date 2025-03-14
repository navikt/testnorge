package no.nav.dolly.consumer.kodeverk.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class KodeverkGetCommand implements Callable<Mono<KodeverkDTO>> {

    private final WebClient webClient;
    private final String kodeverk;
    private final String token;

    @Override
    public Mono<KodeverkDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/kodeverk")
                        .queryParam("kodeverk", kodeverk)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(KodeverkDTO.class)
                .map(kodeverket -> {
                    kodeverket.setStatus(HttpStatus.OK);
                    return kodeverket;
                })
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.just(KodeverkDTO.builder()
                        .kodeverknavn(kodeverk)
                        .status(WebClientFilter.getStatus(error))
                        .message(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
