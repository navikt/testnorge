package no.nav.testnav.apps.statusfrontend.fagsystem.udi;

import java.time.LocalDate;

public record UdiRequest(
        String ident,
        Name navn,
        LocalDate foedselsDato,
        Boolean avgjoerelseUavklart,
        Boolean harOppholdsTillatelse,
        Boolean flyktning,
        String soeknadOmBeskyttelseUnderBehandling,
        LocalDate soknadDato
) {

    public record Name(String fornavn, String mellomnavn, String etternavn) {
    }
}
