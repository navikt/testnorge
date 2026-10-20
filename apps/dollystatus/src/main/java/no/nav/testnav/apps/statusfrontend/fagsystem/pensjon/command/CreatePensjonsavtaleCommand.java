package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.PensjonOperationResponse;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.PensjonsavtaleRequest;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreatePensjonsavtaleCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final RunId runId;
    private final PensjonsavtaleRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return PensjonCommandSupport.requireSuccessful(
                        webClient.post()
                                .uri("/pensjon/api/v2/pensjonsavtale/opprett")
                                .headers(headers -> PensjonCommandSupport.applyHeaders(headers, token, runId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(PensjonOperationResponse.class),
                        Set.copyOf(request.miljoer()))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
