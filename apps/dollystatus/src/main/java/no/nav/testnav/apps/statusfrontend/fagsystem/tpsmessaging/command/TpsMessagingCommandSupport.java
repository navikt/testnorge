package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.TPS_ENVIRONMENT_FAILURE;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.TPS_INCOMPLETE_ENVIRONMENT_STATUS;
import static no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason.TPS_INVALID_RESPONSE;

@Slf4j
final class TpsMessagingCommandSupport {

    private static final Pattern IDENT_PATTERN = Pattern.compile("\\d{11}");

    private TpsMessagingCommandSupport() {
    }

    static Mono<Void> requireSuccessful(JsonNode response, List<String> expectedEnvironments, String operation) {
        if (!response.isArray()) {
            return Mono.error(new FunctionalTestResponseException(TPS_INVALID_RESPONSE));
        }
        var returnedEnvironments = new HashSet<String>();
        var successfulEnvironments = new HashSet<String>();
        for (var status : response) {
            log.info("TPS egenansatt {}: miljo={}, status={}, melding={}, utfyllendeMelding={}",
                    operation,
                    status.path("miljoe").asString(),
                    status.path("status").asString(),
                    maskIdents(status.path("melding").asString()),
                    maskIdents(status.path("utfyllendeMelding").asString()));
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

    private static String maskIdents(String value) {
        return IDENT_PATTERN.matcher(value).replaceAll("***********");
    }
}
