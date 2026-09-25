package no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreatePdlForvalterPersonCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/api/v1/personer")
                .headers(headers -> headers.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateRequest(ident, true))
                .retrieve()
                .bodyToMono(String.class)
                .filter(ident::equals)
                .switchIfEmpty(Mono.error(new IllegalStateException("PDL-opprettingen returnerte ugyldig respons.")))
                .timeout(timeout)
                .then();
    }

    private record CreateRequest(String opprettFraIdent, boolean syntetisk) {
    }
}
