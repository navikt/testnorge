package no.nav.identpool.util;

import no.nav.identpool.ident.ajourhold.util.PersonIdentifikatorUtil;
import no.nav.identpool.ident.exception.UgyldigPersonidentifikatorException;

public final class PersonidentifikatorValidatorUtil {

    public static void valider(String personidentifikator) throws UgyldigPersonidentifikatorException {
        if (!PersonIdentifikatorUtil.gyldigPersonidentifikator(personidentifikator)) {
            throw new UgyldigPersonidentifikatorException("ugyldig personidentifikator");
        }
    }
}
