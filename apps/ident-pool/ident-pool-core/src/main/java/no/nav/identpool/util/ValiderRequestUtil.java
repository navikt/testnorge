package no.nav.identpool.util;

import static java.util.Objects.nonNull;

import java.time.LocalDate;

import lombok.experimental.UtilityClass;
import no.nav.identpool.exception.UgyldigDatoException;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

@UtilityClass
public class ValiderRequestUtil {

    public static void validateDatesInRequest(HentIdenterRequest hentIdenterRequest) {
        if (nonNull(hentIdenterRequest.getFoedtEtter()) &&
                hentIdenterRequest.getFoedtEtter().compareTo(LocalDate.now()) > 0) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtEtter'");
        }
        if (nonNull(hentIdenterRequest.getFoedtFoer()) &&
                hentIdenterRequest.getFoedtFoer().compareTo(LocalDate.now()) > 0) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtFoer'");
        }
    }
}
