package no.nav.testnav.libs.securitycore.command.azuread;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.securitycore.domain.azuread.WellKnown;

@RequiredArgsConstructor
public class GetWellKnownCommand implements Callable<Mono<WellKnown>> {
    private final WebClient webClient;
    private final String baseUrl;

    @Override
    public Mono<WellKnown> call() {
        return webClient
                .get()
                .uri(baseUrl + "/.well-known/openid-configuration")
                .retrieve()
                .bodyToMono(WellKnown.class);
    }
}
