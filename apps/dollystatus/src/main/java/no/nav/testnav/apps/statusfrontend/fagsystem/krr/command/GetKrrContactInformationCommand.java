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
import java.util.ArrayList;
import java.util.List;
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
                                .switchIfEmpty(Mono.error(new IllegalStateException(
                                        "KRR-oppslaget returnerte tom respons.")));
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
            var contactIds = new ArrayList<String>();
            for (var contactInformation : response) {
                contactIds.add(contactId(contactInformation));
                expectedDataPresent |= matches(contactInformation);
                hasMessage |= contactInformation.has("melding");
                hasUnregisteredEntry |= isUnregistered(contactInformation);
            }
            return new KrrResourceStatus(response.isEmpty(), expectedDataPresent,
                    KrrResourceStatus.ResponseShape.ARRAY, response.size(), hasMessage, hasUnregisteredEntry,
                    contactIds);
        }
        if (response.isObject()) {
            return new KrrResourceStatus(false, matches(response),
                    KrrResourceStatus.ResponseShape.OBJECT, response.size(),
                    response.has("melding"), isUnregistered(response), List.of(contactId(response)));
        }
        throw new IllegalStateException("KRR-oppslaget returnerte ugyldig respons.");
    }

    private String contactId(JsonNode contactInformation) {
        if (!contactInformation.isObject() || contactInformation.has("melding")) {
            throw new IllegalStateException("KRR-oppslaget returnerte ugyldig kontaktinformasjon.");
        }
        for (var identField : List.of("personident", "personidentifikator")) {
            if (contactInformation.has(identField)
                    && !expectedRequest.personident().equals(contactInformation.path(identField).asText())) {
                throw new IllegalStateException("KRR-oppslaget returnerte en annen person enn testidenten.");
            }
        }
        var id = contactInformation.path("id");
        if ((!id.isTextual() && !id.isIntegralNumber()) || id.asText().isBlank()) {
            throw new IllegalStateException("KRR-oppslaget mangler kontakt-ID.");
        }
        return id.asText();
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
