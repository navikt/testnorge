package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.identpool.exception.UgyldigDatoException;
import no.nav.testnav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.providers.v1.support.RekvirerIdentRequest;

import java.time.LocalDate;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@UtilityClass
public class ValiderRequestUtil {

    public static void validateDatesInRequest(HentIdenterRequest hentIdenterRequest) {

        if (nonNull(hentIdenterRequest.getFoedtEtter()) &&
                hentIdenterRequest.getFoedtEtter().isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtEtter'");
        }
        if (nonNull(hentIdenterRequest.getFoedtFoer()) &&
                hentIdenterRequest.getFoedtFoer().isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtFoer'");
        }
    }

    public static void validateDatesInRequest(RekvirerIdentRequest request) {

        if (nonNull(request.getFoedtEtter()) &&
                request.getFoedtEtter().isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtEtter'");
        }

        if (nonNull(request.getFoedtFoer()) &&
                request.getFoedtFoer().isAfter(LocalDate.now())) {
            throw new UgyldigDatoException("Kan ikke oppgi framtidig dato i felt 'foedtFoer'");
        }

        if (nonNull(request.getFoedtEtter()) && nonNull(request.getFoedtFoer()) &&
                request.getFoedtEtter().isAfter(request.getFoedtFoer())) {
            throw new UgyldigDatoException("Ugyldig kombinasjon av datoer i feltene 'foedtEtter' og 'foedtFoer'");
        }

        if (nonNull(request.getKjoenn()) && isTrue(request.getPid2032())) {
            throw new UgyldigPersonidentifikatorException("Kan ikke rekvirere pid2032 med kjoenn");
        }
    }
}
