package no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetArbeidssoekerregistreringCommand
        implements Callable<Mono<ArbeidssoekerregisteretResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final ArbeidssoekerregisteretRequest expectedRequest;
    private final Duration timeout;

    @Override
    public Mono<ArbeidssoekerregisteretResourceStatus> call() {
        return webClient.get()
                .uri("/api/v1/arbeidssoekerregistrering/{ident}", expectedRequest.identitetsnummer())
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND
                            || response.statusCode() == HttpStatus.NO_CONTENT) {
                        return response.releaseBody()
                                .thenReturn(ArbeidssoekerregisteretResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::toStatus)
                                .defaultIfEmpty(ArbeidssoekerregisteretResourceStatus.emptyStatus());
                    }
                    return response.createException()
                            .flatMap(exception ->
                                    Mono.<ArbeidssoekerregisteretResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private ArbeidssoekerregisteretResourceStatus toStatus(JsonNode response) {
        if (!response.isObject()) {
            throw new IllegalStateException("Arbeidssøkeroppslaget returnerte ugyldig respons.");
        }
        var expectedDataPresent =
                expectedRequest.utfoertAv().equals(response.path("utfoertAv").asString())
                        && expectedRequest.kilde().equals(response.path("kilde").asString())
                        && expectedRequest.aarsak().equals(response.path("aarsak").asString())
                        && expectedRequest.nuskode().equals(response.path("nuskode").asString())
                        && expectedRequest.jobbsituasjonsbeskrivelse()
                        .equals(response.path("jobbsituasjonsbeskrivelse").asString());
        return new ArbeidssoekerregisteretResourceStatus(false, expectedDataPresent);
    }
}
