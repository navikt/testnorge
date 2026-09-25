package no.nav.testnav.apps.statusfrontend.api.model;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;

import java.time.Instant;
import java.util.UUID;

public record FagsystemStatusResponse(
        String systemId,
        String displayName,
        FunctionalTestEnvironment environment,
        UUID runId,
        FunctionalTestState state,
        Instant startedAt,
        Instant completedAt,
        Instant cachedUntil,
        int cleanupAttempts,
        FunctionalTestErrorResponse error,
        TechnicalStatusResponse technicalStatus
) {

    public static FagsystemStatusResponse from(FunctionalTestStatus status) {
        return new FagsystemStatusResponse(
                status.systemId().value(),
                status.displayName().value(),
                status.environment(),
                status.runId() == null ? null : status.runId().value(),
                status.state(),
                status.startedAt(),
                status.completedAt(),
                status.cachedUntil(),
                status.cleanupAttempts(),
                FunctionalTestErrorResponse.from(status.error()),
                TechnicalStatusResponse.from(status.technicalStatus()));
    }
}
