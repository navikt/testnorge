package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestVerificationTimeoutException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.TimeoutException;

import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.AUTHENTICATION;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.CLEANUP;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.DOWNSTREAM_CLIENT;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.DOWNSTREAM_SERVER;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.EXISTING_DATA;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.INTERNAL;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.NETWORK;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.TIMEOUT;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.VALIDATION;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory.VERIFICATION_TIMEOUT;

final class FunctionalTestErrorSanitizer {

    private FunctionalTestErrorSanitizer() {
    }

    static FunctionalTestError sanitize(FailurePhase phase, Throwable throwable) {
        if (phase == FailurePhase.CLEANUP) {
            return new FunctionalTestError(CLEANUP, "Testdata kunne ikke ryddes opp.");
        }
        if (throwable instanceof FunctionalTestBlockedException) {
            return new FunctionalTestError(EXISTING_DATA, "Eksisterende data hindrer en trygg testkjøring.");
        }
        if (phase == FailurePhase.VERIFY
                && (throwable instanceof FunctionalTestVerificationTimeoutException
                || throwable instanceof TimeoutException)) {
            return new FunctionalTestError(
                    VERIFICATION_TIMEOUT,
                    "Verifiseringen ble ikke ferdig innen tidsfristen.");
        }
        if (throwable instanceof TimeoutException) {
            return new FunctionalTestError(TIMEOUT, "Fagsystemet svarte ikke innen tidsfristen.");
        }
        if (throwable instanceof WebClientRequestException) {
            return new FunctionalTestError(NETWORK, "Fagsystemet kunne ikke nås.");
        }
        if (throwable instanceof WebClientResponseException responseException) {
            return sanitizeResponse(responseException.getStatusCode().value());
        }
        if (throwable instanceof IllegalArgumentException) {
            return new FunctionalTestError(VALIDATION, "Testdata ble avvist av valideringen.");
        }
        return new FunctionalTestError(INTERNAL, messageFor(phase));
    }

    private static FunctionalTestError sanitizeResponse(int statusCode) {
        if (statusCode == HttpStatus.UNAUTHORIZED.value() || statusCode == HttpStatus.FORBIDDEN.value()) {
            return new FunctionalTestError(AUTHENTICATION, "Fagsystemet avviste tjenestetilgangen.");
        }
        if (statusCode >= 500) {
            return new FunctionalTestError(DOWNSTREAM_SERVER, "Fagsystemet returnerte en teknisk feil.");
        }
        return new FunctionalTestError(DOWNSTREAM_CLIENT, "Fagsystemet avviste forespørselen.");
    }

    private static String messageFor(FailurePhase phase) {
        return switch (phase) {
            case PREFLIGHT -> "Forhåndskontrollen feilet.";
            case CREATE -> "Testdata kunne ikke opprettes.";
            case VERIFY -> "Testdata kunne ikke verifiseres.";
            case CLEANUP -> "Testdata kunne ikke ryddes opp.";
        };
    }

    enum FailurePhase {
        PREFLIGHT,
        CREATE,
        VERIFY,
        CLEANUP
    }
}
