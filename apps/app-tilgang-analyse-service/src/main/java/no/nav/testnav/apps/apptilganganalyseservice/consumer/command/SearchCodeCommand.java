package no.nav.testnav.apps.apptilganganalyseservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.dto.SearchDTO;


@Slf4j
@RequiredArgsConstructor
public class SearchCodeCommand implements Callable<Mono<SearchDTO>> {
    private final WebClient webClient;
    private final String search;
    private final int page;
    private final int pageSize;

    @Override
    public Mono<SearchDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/search/code")
                        .queryParam("q", search)
                        .queryParam("per_page", pageSize)
                        .queryParam("page", page)
                        .build()
                )
                .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .retrieve()
                .bodyToMono(SearchDTO.class);

    }
}
