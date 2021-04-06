package no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.dto.SearchDTO;

@Slf4j
@RequiredArgsConstructor
public class SearchCodeCommand implements Callable<SearchDTO> {
    private final WebClient webClient;
    private final String search;

    @Override
    public SearchDTO call() {
        log.info("Soker etter: {}", search);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder.path("/search/code")
                            .queryParam("q", search)
                            .queryParam("par_page", "100")
                            .queryParam("page", "1")
                            .build()
                    )
                    .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                    .retrieve()
                    .bodyToMono(SearchDTO.class)
                    .block();
        } catch (
                WebClientResponseException e) {
            log.error(
                    "Feil ved søk i github: {}.",
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
