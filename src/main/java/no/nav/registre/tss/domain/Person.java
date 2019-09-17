package no.nav.registre.tss.domain;

import static java.lang.Integer.parseInt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Getter
@NoArgsConstructor
public class Person {

    private String fnr;
    private String navn;
    private Integer alder;

    public Person(String fnr, String navn) {
        this.fnr = fnr;
        this.navn = navn;
        this.alder = Math.toIntExact(ChronoUnit.YEARS.between(hentBursdag(fnr), LocalDate.now()));
    }

    /**
     * INDIVID(POS 7-9) 500-749 OG ÅR > 54 => ÅRHUNDRE = 1800
     * INDIVID(POS 7-9) 000-499            => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 900-999 OG ÅR > 39 => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 500-999 OG ÅR < 40 => ÅRHUNDRE = 2000
     */
    private LocalDate hentBursdag(String ident) {
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

        return LocalDate.of(century + year, hentMaaned(ident), hentDag(ident));
    }

    private int hentDag(String ident) {
        // Fix D-number
        return ident.charAt(0) >= '4' ? parseInt(ident.substring(0, 2)) - 40 :
                parseInt(ident.substring(0, 2));
    }

    private int hentMaaned(String ident) {
        // Fix B-number
        return ident.charAt(2) >= '2' ? parseInt(ident.substring(2, 4)) - 20 :
                parseInt(ident.substring(2, 4));
    }
}