package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.time.Instant;
import java.util.Objects;

public record TechnicalStatus(TechnicalStatusState state, Instant checkedAt) {

    public TechnicalStatus {
        Objects.requireNonNull(state);
    }

    public static TechnicalStatus unknown() {
        return new TechnicalStatus(TechnicalStatusState.UNKNOWN, null);
    }
}
