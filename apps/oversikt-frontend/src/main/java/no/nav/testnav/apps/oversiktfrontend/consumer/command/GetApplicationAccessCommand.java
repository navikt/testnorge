package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.oversiktfrontend.consumer.dto.AccessDTO;

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
                .bodyToMono(AccessDTO.class);
    }
}
