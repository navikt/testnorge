package no.nav.identpool.util;

import lombok.experimental.UtilityClass;
import no.nav.identpool.exception.UgyldigDatoException;
import no.nav.identpool.providers.v1.support.HentIdenterRequest;

import java.time.LocalDate;

import static java.util.Objects.nonNull;

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
