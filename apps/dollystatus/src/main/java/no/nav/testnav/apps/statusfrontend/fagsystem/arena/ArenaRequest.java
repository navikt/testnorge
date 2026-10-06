package no.nav.testnav.apps.statusfrontend.fagsystem.arena;

import java.time.LocalDate;
import java.util.List;

public record ArenaRequest(List<User> nyeBrukere) {

    public record User(
            String personident,
            String miljoe,
            LocalDate aktiveringsDato,
            String kvalifiseringsgruppe,
            boolean automatiskInnsendingAvMeldekort
    ) {
    }
}
