package no.nav.dolly.consumer.kodeverk.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
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
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(KodeverkDTO.class)
                .map(kodeverket -> {
                    kodeverket.setStatus(HttpStatus.OK);
                    return kodeverket;
                })
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> {
                    var description = WebClientError.describe(error);
                    return Mono.just(KodeverkDTO.builder()
                            .kodeverknavn(kodeverk)
                            .status(description.getStatus())
                            .message(description.getMessage())
                            .build());
                });
    }
}