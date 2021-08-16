package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;

@Slf4j
@RequiredArgsConstructor
public class GetOppsummeringsdokumentByIdCommand implements Callable<Mono<OppsummeringsdokumentDTO>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String id;

    @SneakyThrows
    @Override
    public Mono<OppsummeringsdokumentDTO> call() {
        log.info("Henter oppsummeringsdokumentet med id {}.", id);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/oppsummeringsdokumenter/{id}")
                        .build(id)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(OppsummeringsdokumentDTO.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.info("Fant ikke oppsummeringsdokumentet med id {}.", id);
                    return Mono.empty();
                });

    }
}
