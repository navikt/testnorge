package no.nav.registre.testnorge.elsam.utils;

import static java.lang.Integer.parseInt;
import static no.nav.registre.testnorge.elsam.utils.Identtype.BOST;
import static no.nav.registre.testnorge.elsam.utils.Identtype.DNR;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import no.nav.registre.testnorge.elsam.consumer.rs.response.aareg.AnsettelsesPeriode;

@Component
@RequiredArgsConstructor
public class DatoUtil {

    @Autowired
    private Random rand;

    public LocalDate lagTilfeldigDatoIAnsettelsesperiode(AnsettelsesPeriode ansettelsesPeriode) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][ ]HH:mm[:ss]");
        LocalDate startDato = LocalDateTime.parse(ansettelsesPeriode.getFom(), dateTimeFormatter).toLocalDate();
        LocalDate sluttDato = null;
        if (ansettelsesPeriode.getTom() != null) {
            sluttDato = LocalDateTime.parse(ansettelsesPeriode.getTom()).toLocalDate();
        }

        int minDag = (int) LocalDate.of(startDato.getYear(), startDato.getMonth(), startDato.getDayOfMonth()).toEpochDay();
        int curDag = (int) LocalDate.now().toEpochDay();
        if (sluttDato != null) {
            curDag = (int) LocalDate.of(sluttDato.getYear(), sluttDato.getMonth(), sluttDato.getDayOfMonth()).toEpochDay();
        }

        long tilfeldigDag = (long) minDag + (long) rand.nextInt(curDag - minDag);

        return LocalDate.ofEpochDay(tilfeldigDag);
    }

    public static LocalDate hentAlderFraFnr(String ident) {
        int year = getFulltAar(ident);
        int month = parseInt(ident.substring(2, 4));
        int day = parseInt(ident.substring(0, 2));

        if (DNR.equals(getIdentType(ident))) {
            day = day - 40;
        }
        if (BOST.equals(getIdentType(ident))) {
            month = month - 20;
        }

        return LocalDate.of(year, month, day);
    }

    private static Identtype getIdentType(String ident) {
        if (parseInt(ident.substring(0, 1)) > 3) {
            return DNR;
        } else if (parseInt(ident.substring(2, 3)) > 1) {
            return BOST;
        }
        return Identtype.FNR;
    }

    /**
     * INDIVID(POS 7-9) 500-749 OG ÅR > 54 => ÅRHUNDRE = 1800
     * INDIVID(POS 7-9) 000-499            => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 900-999 OG ÅR > 39 => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 500-999 OG ÅR < 40 => ÅRHUNDRE = 2000
     */
    private static int getFulltAar(String ident) {
        int year = parseInt(ident.substring(4, 6));
        int individ = parseInt(ident.substring(6, 9));

        // Find century
        int century;
        if (individ < 500 || (individ >= 900 && year > 39)) {
            century = 1900;
        } else if (year < 40) {
            century = 2000;
        } else if (individ < 750 && year > 54) {
            century = 1800;
        } else {
            century = 2000;
        }

        return LocalDate.of(century + year, 1, 1).getYear();
    }
}
