package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.PensjonOperationResponse;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DeletePensjonsavtaleCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final RunId runId;
    private final String ident;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return PensjonCommandSupport.requireSuccessful(
                        webClient.delete()
                                .uri("/pensjon/api/v1/pensjonsavtale/delete")
                                .headers(headers -> PensjonCommandSupport.applyHeaders(headers, token, runId))
                                .header("ident", ident)
                                .retrieve()
                                .bodyToMono(PensjonOperationResponse.class),
                        Set.of("q1", "q2"))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
