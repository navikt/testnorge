package no.nav.testnav.apps.statusfrontend.api.model;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TestRunResponse(
        UUID runId,
        FunctionalTestRunState state,
        Instant startedAt,
        Instant completedAt,
        List<FagsystemStatusResponse> results
) {

    public static TestRunResponse from(FunctionalTestRunStatus status) {
        return new TestRunResponse(
                status.runId().value(),
                status.state(),
                status.startedAt(),
                status.completedAt(),
                status.results().stream()
                        .map(FagsystemStatusResponse::from)
                        .toList());
    }
}
