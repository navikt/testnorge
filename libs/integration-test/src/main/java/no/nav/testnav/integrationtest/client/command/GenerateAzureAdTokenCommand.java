package no.nav.testnav.integrationtest.client.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.securitycore.domain.AccessToken;

@RequiredArgsConstructor
public class GenerateAzureAdTokenCommand implements Callable<Mono<AccessToken>> {
    private final WebClient webClient;
    private final String oid;
    private final String sub;
    private final String audience;

    @Override
    public Mono<AccessToken> call() {
        return webClient.post()
                .uri("/mock/token")
                .body(BodyInserters
                        .fromFormData("oid", oid)
                        .with("sub", sub)
                        .with("audience", audience)
                )
                .retrieve()
                .bodyToMono(AccessToken.class);
    }
}
