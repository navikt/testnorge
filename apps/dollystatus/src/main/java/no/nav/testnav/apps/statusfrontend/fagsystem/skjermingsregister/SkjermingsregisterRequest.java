package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister;

import java.time.LocalDateTime;

public record SkjermingsregisterRequest(
        String etternavn,
        String fornavn,
        String personident,
        LocalDateTime skjermetFra,
        LocalDateTime skjermetTil
) {
}
