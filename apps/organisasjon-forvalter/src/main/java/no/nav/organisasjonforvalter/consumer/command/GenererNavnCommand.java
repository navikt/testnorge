package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GenererNavnCommand implements Callable<Flux<NavnDTO>> {

    private final WebClient webClient;
    private final Integer antall;
    private final String token;

    @Override
    public Flux<NavnDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/navn")
                        .queryParam("antall", antall)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(NavnDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> Mono.empty());
    }

}