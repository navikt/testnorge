package no.nav.testnav.apps.statusfrontend.api.model;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunReference;

import java.util.UUID;

public record RunAcceptedResponse(UUID runId) {

    public static RunAcceptedResponse from(RunReference reference) {
        return new RunAcceptedResponse(reference.runId().value());
    }
}
