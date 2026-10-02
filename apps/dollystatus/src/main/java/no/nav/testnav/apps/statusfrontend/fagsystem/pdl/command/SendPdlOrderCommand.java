package no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.pdl.PdlOrderResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SendPdlOrderCommand implements Callable<Mono<PdlOrderResponse>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final Duration timeout;

    @Override
    public Mono<PdlOrderResponse> call() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/personer/{ident}/ordre")
                        .queryParam("ekskluderEksternePersoner", false)
                        .build(ident))
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(PdlOrderResponse.class)
                .timeout(timeout);
    }
}
