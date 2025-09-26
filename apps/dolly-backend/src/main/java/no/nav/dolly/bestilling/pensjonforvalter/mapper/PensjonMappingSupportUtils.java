package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;

import static no.nav.dolly.util.DateZoneUtil.CET;

@UtilityClass
public class PensjonMappingSupportUtils {

    private static final Random ansatt = new SecureRandom();

    public static LocalDate getForrigeMaaned() {

        var forrigeMaaned = LocalDate.now(CET).minusMonths(1);
        return LocalDate.of(forrigeMaaned.getYear(), forrigeMaaned.getMonth(), 1);
    }

    public static LocalDate getNesteMaaned() {

        var nesteMaaned = LocalDate.now(CET).plusMonths(1);
        return LocalDate.of(nesteMaaned.getYear(), nesteMaaned.getMonth(), 1);
    }

    public static String getRandomAnsatt() {

        return String.format("Z9%05d", ansatt.nextInt(99999));
    }

    public static LocalDate getNesteMaaned(LocalDate dato) {

        var nesteMaaned = dato.plusMonths(1);
        return LocalDate.of(nesteMaaned.getYear(), nesteMaaned.getMonth(), 1);
    }
}