package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class KodeverkCommand implements Callable<Mono<KodeverkDTO>> {

    private final WebClient webClient;
    private final String kodeverknavn;
    private final String token;

    @Override
    public Mono<KodeverkDTO> call() {
        log.info("Henter kodeverk...");
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/kodeverk")
                        .queryParam("kodeverk", kodeverknavn)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(KodeverkDTO.class)
                .doFinally(value -> log.info("Kodeverk {} hentet", kodeverknavn))
                .retryWhen(WebClientError.is5xxException())
                .cache(Duration.ofMinutes(30));
    }

}
