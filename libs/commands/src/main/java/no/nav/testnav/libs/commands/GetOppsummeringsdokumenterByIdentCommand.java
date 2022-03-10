package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOppsummeringsdokumenterByIdentCommand implements Callable<Mono<List<OppsummeringsdokumentDTO>>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String ident;
    private final String miljo;

    @SneakyThrows
    @Override
    public Mono<List<OppsummeringsdokumentDTO>> call() {
        log.info("Henter oppsummeringsdokumenteter for ident {}.", ident);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/oppsummeringsdokumenter/identer/{ident}")
                        .build(ident)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", this.miljo)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new RuntimeException("Noe gikk galt med henting av oppsummeringsdokumenteter for " + ident)))
                .bodyToMono(new ParameterizedTypeReference<List<OppsummeringsdokumentDTO>>() {})
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .map(value -> {
                    log.info("Hentet {} oppsummeringsdokumenter funnet for {}", value.size(), ident);
                    return value;
                });

    }
}
