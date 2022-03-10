package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.AccessDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetApplicationAccessCommand implements Callable<Mono<AccessDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String name;
    private final String repo;

    @Override
    public Mono<AccessDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/applications/{name}/access")
                        .queryParam("owner", "navikt")
                        .queryParam("repo", repo)
                        .build(name)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(AccessDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
