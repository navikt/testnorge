package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentetDTO;

@Slf4j
@DependencyOn("testnorge-arbeidsforhold-api")
@RequiredArgsConstructor
public class GetOppsummeringsdokumentetCommand implements Callable<OppsummeringsdokumentetDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String orgnummer;
    private final LocalDate kalendermaaned;
    private final String miljo;

    @SneakyThrows
    @Override
    public OppsummeringsdokumentetDTO call() {
        log.info("Henter oppsummeringsdokumentet med orgnummer {}.", orgnummer);
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
                    .bodyToMono(OppsummeringsdokumentetDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}