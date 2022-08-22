package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.BrukerDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetBrukerCommand implements Callable<Mono<BrukerDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String orgnummer;

    @Override
    public Mono<BrukerDTO> call() {
        return webClient
                .get()
                .uri(builder ->builder.path("/api/v1/brukere").queryParam("organisasjonsnummer", orgnummer).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(BrukerDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .next();
    }
}
