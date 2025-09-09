package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;

@UtilityClass
public class PensjonMappingSupportUtils {

    private static final Random ansatt = new SecureRandom();

    public static LocalDate getForrigeMaaned() {

        var forrigeMaaned = LocalDate.now().minusMonths(1);
        return LocalDate.of(forrigeMaaned.getYear(), forrigeMaaned.getMonth(), 1);
    }

    public static LocalDate getNesteMaaned() {

        var nesteMaaned = LocalDate.now().plusMonths(1);
        return LocalDate.of(nesteMaaned.getYear(), nesteMaaned.getMonth(), 1);
    }

    public static String getRandomAnsatt() {

        return String.format("Z9%05d", ansatt.nextInt(99999));
    }
}