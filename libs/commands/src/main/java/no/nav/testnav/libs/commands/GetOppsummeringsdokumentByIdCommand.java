package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;

@Slf4j
@RequiredArgsConstructor
public class GetOppsummeringsdokumentByIdCommand implements Callable<OppsummeringsdokumentDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String id;

    @SneakyThrows
    @Override
    public OppsummeringsdokumentDTO call() {
        log.info("Henter oppsummeringsdokumentet med id {}.", id);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/oppsummeringsdokumenter/{id}")
                            .build(id)
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OppsummeringsdokumentDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke oppsummeringsdokument med id {}.", id);
            return null;
        }
    }
}
