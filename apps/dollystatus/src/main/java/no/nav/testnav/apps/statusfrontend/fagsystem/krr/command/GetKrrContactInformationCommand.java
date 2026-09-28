package no.nav.testnav.apps.statusfrontend.fagsystem.krr.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetKrrContactInformationCommand implements Callable<Mono<KrrResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final KrrRequest expectedRequest;
    private final Duration timeout;

    @Override
    public Mono<KrrResourceStatus> call() {
        return webClient.post()
                .uri("/krrstub/api/v2/person/kontaktinformasjon/soek")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .header("Nav-Consumer-Id", "Dolly")
                .bodyValue(Map.of("personidentifikator", expectedRequest.personident()))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND
                            || response.statusCode() == HttpStatus.NO_CONTENT) {
                        return response.releaseBody().thenReturn(KrrResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::toStatus)
                                .defaultIfEmpty(KrrResourceStatus.emptyStatus());
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<KrrResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private KrrResourceStatus toStatus(JsonNode response) {
        if (response.isArray()) {
            var expectedDataPresent = false;
            var hasMessage = false;
            var hasUnregisteredEntry = false;
            for (var contactInformation : response) {
                expectedDataPresent |= matches(contactInformation);
                hasMessage |= contactInformation.has("melding");
                hasUnregisteredEntry |= isUnregistered(contactInformation);
            }
            return new KrrResourceStatus(response.isEmpty(), expectedDataPresent,
                    KrrResourceStatus.ResponseShape.ARRAY, response.size(), hasMessage, hasUnregisteredEntry);
        }
        if (response.isObject()) {
            return new KrrResourceStatus(false, matches(response),
                    KrrResourceStatus.ResponseShape.OBJECT, response.size(),
                    response.has("melding"), isUnregistered(response));
        }
        throw new IllegalStateException("KRR-oppslaget returnerte ugyldig respons.");
    }

    private static boolean isUnregistered(JsonNode response) {
        return response.path("registrert").isBoolean() && !response.path("registrert").asBoolean();
    }

    private boolean matches(JsonNode response) {
        return (response.path("personident").isMissingNode()
                || expectedRequest.personident().equals(response.path("personident").asString()))
                && expectedRequest.mobil().equals(response.path("mobil").asString())
                && expectedRequest.epost().equals(response.path("epost").asString())
                && expectedRequest.spraak().equals(response.path("spraak").asString())
                && expectedRequest.reservert() == response.path("reservert").asBoolean()
                && expectedRequest.registrert() == response.path("registrert").asBoolean();
    }
}
