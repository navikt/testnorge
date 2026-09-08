package no.nav.testnav.apps.tenorsearchservice.domain;

import java.time.Instant;

public record TenorMalUpsertResult(
        Long id,
        String malNavn,
        String malNavnNormalisert,
        TenorMalType malType,
        String soekKriterier,
        Long brukerId,
        Instant opprettet,
        Instant sistOppdatert,
        boolean ny
) {

    public TenorMal toTenorMal() {
        return new TenorMal(
                id,
                malNavn,
                malNavnNormalisert,
                malType,
                soekKriterier,
                brukerId,
                opprettet,
                sistOppdatert);
    }
}
