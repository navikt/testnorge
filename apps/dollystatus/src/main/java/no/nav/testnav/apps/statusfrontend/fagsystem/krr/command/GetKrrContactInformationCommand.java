package no.nav.testnav.apps.statusfrontend.fagsystem.krr.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
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

import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.KRR_EMPTY_RESPONSE;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.KRR_INVALID_CONTACT_INFORMATION;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.KRR_INVALID_RESPONSE;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.KRR_MISSING_CONTACT_ID;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.KRR_UNEXPECTED_PERSON;

@RequiredArgsConstructor
@Slf4j
public class GetKrrContactInformationCommand implements Callable<Mono<KrrResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final KrrRequest expectedRequest;
    private final RunId runId;
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
                                .switchIfEmpty(Mono.error(new FunctionalTestResponseException(KRR_EMPTY_RESPONSE)));
                    }
                    return response.createException()
                            .flatMap(Mono::error);
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
        throw new FunctionalTestResponseException(KRR_INVALID_RESPONSE);
    }

    private String contactId(JsonNode contactInformation) {
        if (!contactInformation.isObject() || contactInformation.has("melding")) {
            throw new FunctionalTestResponseException(KRR_INVALID_CONTACT_INFORMATION);
        }
        for (var identField : List.of("personident", "personidentifikator")) {
            if (contactInformation.has(identField)
                    && !expectedRequest.personident().equals(contactInformation.path(identField).asText())) {
                throw new FunctionalTestResponseException(KRR_UNEXPECTED_PERSON);
            }
        }
        var id = contactInformation.path("id");
        if ((!id.isString() && !id.isIntegralNumber()) || id.asString().isBlank()) {
            var registered = contactInformation.path("registrert");
            log.warn(
                    "KRR-oppslag mangler brukbar kontakt-ID: runId={}, idType={}, idBlank={}, registrert={}, hasContactData={}",
                    runId.value(),
                    id.getNodeType(),
                    id.isString() && id.asString().isBlank(),
                    registered.isBoolean() ? registered.asBoolean() : null,
                    !contactInformation.path("mobil").asString("").isBlank()
                            || !contactInformation.path("epost").asString("").isBlank()
                            || !contactInformation.path("sdpAdresse").asString("").isBlank());
            throw new FunctionalTestResponseException(KRR_MISSING_CONTACT_ID);
        }
        return id.asString();
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

    private static boolean isUnregistered(JsonNode response) {
        return response.path("registrert").isBoolean() && !response.path("registrert").asBoolean();
    }
}
