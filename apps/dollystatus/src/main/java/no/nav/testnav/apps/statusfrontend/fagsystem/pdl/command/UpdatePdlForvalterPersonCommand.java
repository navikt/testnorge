package no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class UpdatePdlForvalterPersonCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.put()
                .uri("/api/v1/personer/{ident}", ident)
                .headers(headers -> headers.setBearerAuth(token))
                .header("relaxed", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateRequest(new Person(List.of(new Name("TEST", "TESTESEN")))))
                .retrieve()
                .bodyToMono(String.class)
                .filter(ident::equals)
                .switchIfEmpty(Mono.error(new IllegalStateException("PDL-oppdateringen returnerte ugyldig respons.")))
                .timeout(timeout)
                .then();
    }

    private record UpdateRequest(Person person) {
    }

    private record Person(List<Name> navn) {
    }

    private record Name(String fornavn, String etternavn) {
    }
}
