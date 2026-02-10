package no.nav.testnav.joarkdokumentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetDokumentCommand implements Callable<Mono<String>> {
    private final WebClient webClient;
    private final String token;
    private final String journalpostId;
    private final String dokumentInfoId;
    private final String miljo;
    private final DokumentType type;

    @Override
    public Mono<String> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/saf/{miljo}/rest/hentdokument/{journalpostId}/{dokumentInfoId}/{type}")
                        .build(miljo, journalpostId, dokumentInfoId, type.name()))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }

}
