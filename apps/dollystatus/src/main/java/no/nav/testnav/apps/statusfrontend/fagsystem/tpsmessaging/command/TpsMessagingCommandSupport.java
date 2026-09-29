package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command;

import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;

import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.TPS_ENVIRONMENT_FAILURE;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.TPS_INCOMPLETE_ENVIRONMENT_STATUS;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.TPS_INVALID_RESPONSE;

final class TpsMessagingCommandSupport {

    private TpsMessagingCommandSupport() {
    }

    static Mono<Void> requireSuccessful(JsonNode response, List<String> expectedEnvironments) {
        if (!response.isArray()) {
            return Mono.error(new FunctionalTestResponseException(TPS_INVALID_RESPONSE));
        }
        var returnedEnvironments = new HashSet<String>();
        var successfulEnvironments = new HashSet<String>();
        for (var status : response) {
            returnedEnvironments.add(status.path("miljoe").asString());
            if ("OK".equals(status.path("status").asString())) {
                successfulEnvironments.add(status.path("miljoe").asString());
            }
        }
        return successfulEnvironments.containsAll(expectedEnvironments)
                ? Mono.empty()
                : Mono.error(new FunctionalTestResponseException(
                        returnedEnvironments.containsAll(expectedEnvironments)
                                ? TPS_ENVIRONMENT_FAILURE
                                : TPS_INCOMPLETE_ENVIRONMENT_STATUS));
    }
}
