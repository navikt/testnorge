package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;

public record RunReference(RunId runId, boolean nyKjoring) {

    public RunReference {
        Objects.requireNonNull(runId);
    }

    public RunReference(RunId runId) {
        this(runId, true);
    }
}
