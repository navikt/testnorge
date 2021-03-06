package no.nav.testnav.joarkdokumentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.joarkdokumentservice.domain.DokumentType;

@RequiredArgsConstructor
public class GetDokumentCommand implements Callable<Mono<String>> {
    private final WebClient webClient;
    private final String token;
    private final Integer journalpostId;
    private final Integer dokumentInfoId;
    private final String miljo;
    private final DokumentType type;

    @Override
    public Mono<String> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{miljo}/rest/hentdokument/{journalpostId}/{dokumentInfoId}/{type}")
                        .build(miljo, journalpostId, dokumentInfoId, type.name())
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty()
                );
    }
}
