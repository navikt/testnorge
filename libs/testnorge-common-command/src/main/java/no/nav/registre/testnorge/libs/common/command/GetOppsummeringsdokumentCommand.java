package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentDTO;

@Slf4j
@RequiredArgsConstructor
public class GetOppsummeringsdokumentCommand implements Callable<OppsummeringsdokumentDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String orgnummer;
    private final LocalDate kalendermaaned;
    private final String miljo;

    @SneakyThrows
    @Override
    public OppsummeringsdokumentDTO call() {
        log.info("Henter oppsummeringsdokumentet med orgnummer {} den {}.", orgnummer, kalendermaaned);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/oppsummeringsdokumenter/{orgnummer}/{kalendermaaned}")
                            .build(orgnummer, kalendermaaned)
                    )
                    .header("miljo", this.miljo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OppsummeringsdokumentDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}