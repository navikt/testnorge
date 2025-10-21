package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.identpool.exception.UgyldigDatoException;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.providers.v1.support.RekvirerIdentRequest;

import java.time.LocalDate;

import static java.util.Objects.nonNull;

@UtilityClass
public class ValiderRequestUtil {

    public static void validateDatesInRequest(HentIdenterRequest hentIdenterRequest) {

        validerFoedtEtter(hentIdenterRequest.getFoedtEtter());
        validerFoedtFoer(hentIdenterRequest.getFoedtFoer());
        validateDateOrder(hentIdenterRequest.getFoedtEtter(), hentIdenterRequest.getFoedtFoer());
    }

    public static void validateDatesInRequest(RekvirerIdentRequest request) {

        validerFoedtEtter(request.getFoedtEtter());
        validerFoedtFoer(request.getFoedtFoer());
        validateDateOrder(request.getFoedtEtter(), request.getFoedtFoer());
    }

    private static void validerFoedtEtter(LocalDate foedtEtter) {

        if (nonNull(foedtEtter) && foedtEtter.isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtEtter'");
        }
    }

    private static void validerFoedtFoer(LocalDate foedtEtter) {

        if (nonNull(foedtEtter) && foedtEtter.isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtFoer'");
        }
    }

    private static void validateDateOrder(LocalDate foedtEtter, LocalDate foedtFoer) {

        if (nonNull(foedtEtter) && nonNull(foedtFoer) &&
                foedtEtter.isAfter(foedtFoer)) {
            throw new UgyldigDatoException("Ugyldig kombinasjon av datoer i feltene 'foedtEtter' og 'foedtFoer'");
        }
    }
}
