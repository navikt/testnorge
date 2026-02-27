package no.nav.dolly.domain.resultset.entity.testgruppe;

import no.nav.dolly.domain.jpa.Testident;

public record RsIdentMaster(
        String ident,
        Testident.Master master
) {
}
