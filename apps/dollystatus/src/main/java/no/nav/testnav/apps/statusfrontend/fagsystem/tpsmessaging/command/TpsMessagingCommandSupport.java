package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;

final class TpsMessagingCommandSupport {

    private TpsMessagingCommandSupport() {
    }

    static Mono<Void> requireSuccessful(JsonNode response, List<String> expectedEnvironments) {
        if (!response.isArray()) {
            return Mono.error(new IllegalStateException("TPS Messaging returnerte ugyldig respons."));
        }
        var successfulEnvironments = new HashSet<String>();
        for (var status : response) {
            if ("OK".equals(status.path("status").asString())) {
                successfulEnvironments.add(status.path("miljoe").asString());
            }
        }
        return successfulEnvironments.containsAll(expectedEnvironments)
                ? Mono.empty()
                : Mono.error(new IllegalStateException(
                "TPS Messaging returnerte ufullstendig miljøstatus."));
    }
}
