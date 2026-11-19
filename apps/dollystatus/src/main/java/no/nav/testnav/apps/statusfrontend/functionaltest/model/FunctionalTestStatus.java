package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.nonNull;

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

    public boolean hasProblem() {
        return switch (state) {
            case PREFLIGHT_FAILED, CREATE_FAILED, VERIFY_FAILED, VERIFY_TIMEOUT, CLEANUP_FAILED, BLOCKED -> true;
            case TECHNICAL_ONLY -> nonNull(technicalStatus)
                    && technicalStatus.state() == TechnicalStatusState.DOWN;
            default -> false;
        };
    }

    public boolean isFailure() {
        return switch (state) {
            case PREFLIGHT_FAILED, CREATE_FAILED, VERIFY_FAILED, VERIFY_TIMEOUT -> true;
            default -> false;
        };
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
