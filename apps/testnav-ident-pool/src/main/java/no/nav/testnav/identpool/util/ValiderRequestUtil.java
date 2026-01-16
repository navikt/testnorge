package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.identpool.exception.UgyldigDatoException;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.dto.IdentpoolRequestDTO;

import java.time.LocalDate;

import static java.util.Objects.nonNull;

@UtilityClass
public class ValiderRequestUtil {

    public static void validateDatesInRequest(HentIdenterRequest hentIdenterRequest) {

        validerDatoer(hentIdenterRequest.getFoedtEtter(), hentIdenterRequest.getFoedtFoer());
    }

    public static void validateDatesInRequest(IdentpoolRequestDTO request) {

        validerDatoer(request.foedtEtter(), request.foedtFoer());
    }

    private static void validerDatoer(LocalDate foedtEtter, LocalDate foedtFoer) {

        if (nonNull(foedtEtter) && foedtEtter.isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtEtter'");
        }

        if (nonNull(foedtFoer) && foedtFoer.isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtFoer'");
        }

        if (nonNull(foedtEtter) && nonNull(foedtFoer) &&
                foedtEtter.isAfter(foedtFoer)) {
            throw new UgyldigDatoException("Ugyldig kombinasjon av datoer i feltene 'foedtEtter' og 'foedtFoer'");
        }
    }
}
