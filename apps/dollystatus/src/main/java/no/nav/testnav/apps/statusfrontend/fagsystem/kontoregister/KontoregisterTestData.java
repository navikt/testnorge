package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister;

import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;

final class KontoregisterTestData {

    private static final String ACCOUNT_NUMBER = "12345678903";

    private KontoregisterTestData() {
    }

    static OppdaterKontoRequestDTO account(String ident) {
        return new OppdaterKontoRequestDTO(ident, ACCOUNT_NUMBER, "Dolly", null);
    }
}
