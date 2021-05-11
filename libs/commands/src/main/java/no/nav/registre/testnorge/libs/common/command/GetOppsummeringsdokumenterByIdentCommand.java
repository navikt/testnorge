package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;

@Slf4j
@RequiredArgsConstructor
public class GetOppsummeringsdokumenterByIdentCommand implements Callable<List<OppsummeringsdokumentDTO>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String ident;
    private final String miljo;

    @SneakyThrows
    @Override
    public List<OppsummeringsdokumentDTO> call() {
        log.info("Henter oppsummeringsdokumenteter for ident {}.", ident);
        try {
            var response = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/oppsummeringsdokumenter/identer/{ident}")
                            .build(ident)
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("miljo", this.miljo)
                    .retrieve()
                    .bodyToMono(OppsummeringsdokumentDTO[].class)
                    .block();
            return Arrays.asList(response);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke oppsummeringsdokumenteter med for ident {}.", ident);
            return null;
        }
    }
}
