package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;

public record RunReference(RunId runId) {

    public RunReference {
        Objects.requireNonNull(runId);
    }
}
