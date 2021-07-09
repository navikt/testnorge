package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;

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
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound, throwable -> Mono.empty());
    }
}