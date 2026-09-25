package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record FunctionalTestRunStatus(
        RunId runId,
        FunctionalTestRunState state,
        Instant startedAt,
        Instant completedAt,
        List<FunctionalTestStatus> results
) {

    public FunctionalTestRunStatus {
        Objects.requireNonNull(runId);
        Objects.requireNonNull(state);
        Objects.requireNonNull(startedAt);
        results = List.copyOf(results);
    }
}
