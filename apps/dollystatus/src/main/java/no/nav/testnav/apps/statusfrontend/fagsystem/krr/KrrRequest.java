package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

import java.time.ZonedDateTime;

public record KrrRequest(
        String personident,
        ZonedDateTime gyldigFra,
        boolean reservert,
        boolean registrert,
        String mobil,
        String epost,
        String spraak,
        ZonedDateTime epostOppdatert,
        ZonedDateTime epostVerifisert,
        ZonedDateTime mobilOppdatert,
        ZonedDateTime mobilVerifisert,
        ZonedDateTime spraakOppdatert,
        ZonedDateTime reservertOppdatert
) {
}
