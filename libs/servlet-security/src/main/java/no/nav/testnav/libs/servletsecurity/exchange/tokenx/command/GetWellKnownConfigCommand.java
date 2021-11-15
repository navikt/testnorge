package no.nav.testnav.libs.servletsecurity.exchange.tokenx.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.servletsecurity.domain.v1.WellKnown;

@RequiredArgsConstructor
public class GetWellKnownConfigCommand implements Callable<Mono<WellKnown>> {
    private final WebClient webClient;
    private final String url;

    @Override
    public Mono<WellKnown> call() {
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(WellKnown.class);
    }
}
