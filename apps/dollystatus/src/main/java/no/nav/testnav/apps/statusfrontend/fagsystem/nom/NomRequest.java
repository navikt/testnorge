package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import java.time.LocalDate;

public record NomRequest(
        String personident,
        String etternavn,
        String fornavn,
        String mellomnavn,
        LocalDate startDato,
        LocalDate sluttDato
) {
}
