package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.time.Instant;
import java.util.Objects;

public record FunctionalTestStatus(
        SystemId systemId,
        DisplayName displayName,
        FunctionalTestEnvironment environment,
        RunId runId,
        FunctionalTestState state,
        Instant startedAt,
        Instant completedAt,
        Instant cachedUntil,
        int cleanupAttempts,
        FunctionalTestError error,
        TechnicalStatus technicalStatus
) {

    public FunctionalTestStatus {
        Objects.requireNonNull(systemId);
        Objects.requireNonNull(displayName);
        Objects.requireNonNull(environment);
        Objects.requireNonNull(state);
        Objects.requireNonNull(technicalStatus);
        if (cleanupAttempts < 0) {
            throw new IllegalArgumentException("Cleanup attempts cannot be negative.");
        }
    }

    public static FunctionalTestStatus notRun(
            FunctionalTestKey key,
            DisplayName displayName,
            TechnicalStatus technicalStatus
    ) {
        return new FunctionalTestStatus(
                key.systemId(),
                displayName,
                key.environment(),
                null,
                FunctionalTestState.NOT_RUN,
                null,
                null,
                null,
                0,
                null,
                technicalStatus);
    }
}
