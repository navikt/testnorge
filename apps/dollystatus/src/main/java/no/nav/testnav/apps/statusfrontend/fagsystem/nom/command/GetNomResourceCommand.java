package no.nav.testnav.apps.statusfrontend.fagsystem.nom.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.NomRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.NomResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetNomResourceCommand implements Callable<Mono<NomResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final NomRequest expectedRequest;
    private final Duration timeout;

    @Override
    public Mono<NomResourceStatus> call() {
        return webClient.post()
                .uri("/api/v1/dolly/hentRessurs")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(expectedRequest.personident())
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND
                            || response.statusCode() == HttpStatus.NO_CONTENT) {
                        return response.releaseBody().thenReturn(NomResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::toStatus)
                                .defaultIfEmpty(NomResourceStatus.emptyStatus());
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<NomResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private NomResourceStatus toStatus(JsonNode response) {
        if (!response.isObject()) {
            throw new IllegalStateException("NOM-oppslaget returnerte ugyldig respons.");
        }
        var endDate = response.path("sluttDato").isTextual()
                ? LocalDate.parse(response.path("sluttDato").asString())
                : null;
        var resourceId = response.path("fid").asText(null);
        var expected = expectedRequest.personident().equals(response.path("personident").asString())
                && expectedRequest.fornavn().equals(response.path("navn").path("fornavn").asString())
                && expectedRequest.etternavn().equals(response.path("navn").path("etternavn").asString())
                && resourceId != null
                && !resourceId.isBlank()
                && expectedRequest.startDato().equals(
                        LocalDate.parse(response.path("startDato").asString()));
        return new NomResourceStatus(
                false,
                expected && endDate == null,
                endDate != null,
                resourceId,
                endDate);
    }
}
