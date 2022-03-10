package no.nav.testnav.apps.apptilganganalyseservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.apptilganganalyseservice.consumer.dto.BlobDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.Callable;


@Slf4j
@RequiredArgsConstructor
public class GetBlobFromShaCommand implements Callable<Mono<byte[]>> {
    private final WebClient webClient;
    private final String sha;
    private final String owner;
    private final String repo;

    @Override
    public Mono<byte[]> call() {
        log.info("Henter blob fra sha {} fra {}/{}.", sha, owner, repo);
        return webClient
                .get()
                .uri(builder -> builder.path("/repos/{owner}/{repo}/git/blobs/{sha}").build(owner, repo, sha))
                .retrieve()
                .bodyToMono(BlobDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .map(dto -> Base64.getMimeDecoder().decode(dto.getContent()));
    }
}