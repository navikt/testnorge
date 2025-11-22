package no.nav.testnav.joarkdokumentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetPDFCommand implements Callable<Mono<byte[]>> {

    private final WebClient webClient;
    private final String token;
    private final String journalpostId;
    private final String dokumentInfoId;
    private final String miljo;

    @Override
    public Mono<byte[]> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/saf/{miljo}/rest/hentdokument/{journalpostId}/{dokumentInfoId}/ARKIV")
                        .build(miljo, journalpostId, dokumentInfoId)
                )
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .accept(MediaType.APPLICATION_PDF)
                .retrieve()
                .bodyToMono(byte[].class)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty()
                );
    }
}
