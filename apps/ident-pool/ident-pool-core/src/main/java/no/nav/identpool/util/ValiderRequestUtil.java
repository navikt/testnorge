package no.nav.identpool.util;

import java.time.LocalDate;

import no.nav.identpool.exception.UgyldigDatoException;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

public class ValiderRequestUtil {

    public static void validateDatesInRequest(HentIdenterRequest hentIdenterRequest) throws UgyldigDatoException {
        if (hentIdenterRequest.getFoedtEtter().compareTo(LocalDate.now()) > 0) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtEtter'");
        }
        if (hentIdenterRequest.getFoedtFoer() != null && hentIdenterRequest.getFoedtFoer().compareTo(LocalDate.now()) > 0) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtFoer'");
        }
    }
}
