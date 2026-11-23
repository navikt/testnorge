package no.nav.testnav.apps.statusfrontend.api.model;

import java.time.Instant;
import java.util.List;

public record StatusoversiktResponse(
        Status status,
        boolean kjoringPagaar,
        Instant sistOppdatert,
        Instant nesteAutomatiskeKjoring,
        Instant nesteManuelleKjoringTidligst,
        List<Problem> problemer
) {

    public enum Status {
        OK,
        ADVARSEL,
        FEIL,
        UKJENT
    }

    public record Problem(
            String systemId,
            String navn,
            String miljo,
            String tilstand,
            String melding,
            Instant tidspunkt
    ) {
    }
}
