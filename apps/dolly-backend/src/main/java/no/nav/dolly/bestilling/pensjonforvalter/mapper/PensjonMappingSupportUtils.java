package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.PdlPerson;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;

import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@UtilityClass
public class PensjonMappingSupportUtils {

    private static final Random ansatt = new SecureRandom();

    static LocalDate getForrigeMaaned() {

        var forrigeMaaned = LocalDate.now().minusMonths(1);
        return LocalDate.of(forrigeMaaned.getYear(), forrigeMaaned.getMonth(), 1);
    }

    static LocalDate getNesteMaaned() {

        var nesteMaaned = LocalDate.now().plusMonths(1);
        return LocalDate.of(nesteMaaned.getYear(), nesteMaaned.getMonth(), 1);
    }

    static String getRandomAnsatt() {

        return String.format("Z9%05d", ansatt.nextInt(99999));
    }

    static LocalDate getFoedselsdato(PdlPerson.Foedsel foedsel) {

        return nullcheckSetDefaultValue(foedsel.getFoedselsdato(),
                LocalDate.of(foedsel.getFoedselsaar(), 1, 1));
    }
}
