package no.nav.testnav.apps.statusfrontend.api.model;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;

import java.time.Instant;

public record TechnicalStatusResponse(TechnicalStatusState state, Instant checkedAt) {

    static TechnicalStatusResponse from(TechnicalStatus status) {
        return new TechnicalStatusResponse(status.state(), status.checkedAt());
    }
}
