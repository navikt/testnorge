package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.utils.WebClientFilter;
import no.nav.testnav.libs.dto.geografiskekodeverkservice.v1.GeografiskeKodeverkDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GeografiskeKodeverkCommand implements Callable<Flux<GeografiskeKodeverkDTO>> {

    private final WebClient webClient;
    private final String url;
    private final String query;
    private final String token;

    @Override
    public Flux<GeografiskeKodeverkDTO> call() {
        log.info("Henter geografiske kodeverk...");
        return webClient
                .get()
                .uri(builder -> builder.path(url).query(query).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(GeografiskeKodeverkDTO.class)
                .doFinally(value -> log.info("Geografiske kodeverk hentet."))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
