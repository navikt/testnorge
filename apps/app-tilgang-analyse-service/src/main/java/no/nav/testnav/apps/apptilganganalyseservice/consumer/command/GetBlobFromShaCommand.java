package no.nav.testnav.apps.apptilganganalyseservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.concurrent.Callable;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.dto.BlobDTO;


@Slf4j
@RequiredArgsConstructor
public class GetBlobFromShaCommand implements Callable<Mono<byte[]>> {
    private final WebClient webClient;
    private final String sha;
    private final String repo;

    @Override
    public Mono<byte[]> call() {
        log.info("Henter blob med sha: {} fra repo: {}", sha, repo);
        return webClient
                .get()
                .uri(builder -> builder.path("/repos/" + repo + "/git/blobs/{sha}").build(sha))
                .retrieve()
                .bodyToMono(BlobDTO.class)
                .map(dto -> Base64.getMimeDecoder().decode(dto.getContent()));
    }
}