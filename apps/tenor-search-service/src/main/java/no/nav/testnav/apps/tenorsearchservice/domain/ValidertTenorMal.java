package no.nav.testnav.apps.tenorsearchservice.domain;

public record ValidertTenorMal(
        String malNavn,
        String malNavnNormalisert,
        TenorMalType malType,
        String soekKriterier
) {
}
