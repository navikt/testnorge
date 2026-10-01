package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;

@UtilityClass
class KontoregisterTestData {

    private static final String ACCOUNT_NUMBER = "12345678903";

    static OppdaterKontoRequestDTO account(String ident) {
        return new OppdaterKontoRequestDTO(ident, ACCOUNT_NUMBER, "Dolly", null);
    }
}
