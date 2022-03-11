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
public class GetBlobFromPathAndRefCommand implements Callable<Mono<byte[]>> {
    private final WebClient webClient;
    private final String path;
    private final String ref;
    private final String repo;
    private final String owner;

    @Override
    public Mono<byte[]> call() {
        log.info("Henter blob fra path {} fra {}/{} (ref:{}).", path, owner, repo, ref);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/repos/{owner}/{repo}/contents/{path}")
                        .queryParam("ref", ref)
                        .build(owner, repo, path)
                )
                .retrieve()
                .bodyToMono(BlobDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .map(blob -> Base64.getMimeDecoder().decode(blob.getContent()));
    }
}