package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.PensjonOperationResponse;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.TpForholdRequest;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateTpForholdCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final RunId runId;
    private final TpForholdRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return PensjonCommandSupport.requireSuccessful(
                        webClient.post()
                                .uri("/pensjon/api/v1/tp/forhold")
                                .headers(headers -> PensjonCommandSupport.applyHeaders(headers, token, runId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(PensjonOperationResponse.class),
                        request.miljoer())
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
