package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOppsummeringsdokumentCommand implements Callable<Mono<OppsummeringsdokumentDTO>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String orgnummer;
    private final LocalDate kalendermaaned;
    private final String miljo;

    @SneakyThrows
    @Override
    public Mono<OppsummeringsdokumentDTO> call() {
        log.info("Henter oppsummeringsdokumentet med orgnummer {} den {} i {}.", orgnummer, kalendermaaned, miljo);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/oppsummeringsdokumenter/{orgnummer}/{kalendermaaned}")
                        .build(orgnummer, kalendermaaned)
                )
                .header("miljo", miljo)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(OppsummeringsdokumentDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.info("Fant ikke oppsummeringsdokumentet med orgnummer {} den {} i {}.", orgnummer, kalendermaaned, miljo);
                    return Mono.empty();
                });
    }
}