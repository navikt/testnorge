package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import java.time.LocalDate;

public record NomResourceStatus(
        boolean empty,
        boolean expectedPersonPresent,
        boolean closed,
        String resourceId,
        LocalDate startDate,
        LocalDate endDate
) {

    public static NomResourceStatus emptyStatus() {
        return new NomResourceStatus(true, false, false, null, null, null);
    }
}
