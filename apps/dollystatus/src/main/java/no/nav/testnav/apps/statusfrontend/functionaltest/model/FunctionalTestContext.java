package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.time.Instant;
import java.util.Objects;

public record FunctionalTestContext(
        RunId runId,
        SystemId systemId,
        FunctionalTestEnvironment environment,
        Instant startedAt
) {

    public FunctionalTestContext {
        Objects.requireNonNull(runId);
        Objects.requireNonNull(systemId);
        Objects.requireNonNull(environment);
        Objects.requireNonNull(startedAt);
    }
}
