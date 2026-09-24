package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import java.time.LocalDate;

public record NomResourceStatus(
        boolean empty,
        boolean expectedDataPresent,
        boolean closed,
        String resourceId,
        LocalDate endDate
) {

    public static NomResourceStatus emptyStatus() {
        return new NomResourceStatus(true, false, false, null, null);
    }
}
