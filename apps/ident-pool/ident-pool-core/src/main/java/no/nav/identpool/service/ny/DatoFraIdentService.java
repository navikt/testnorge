package no.nav.identpool.service.ny;

import static java.lang.Integer.parseInt;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

/**
 * INDIVID(POS 7-9) 500-749 OG �R > 54 => �RHUNDRE = 1800
 * INDIVID(POS 7-9) 000-499            => �RHUNDRE = 1900
 * INDIVID(POS 7-9) 900-999 OG �R > 39 => �RHUNDRE = 1900
 * INDIVID(POS 7-9) 500-999 OG �R < 40 => �RHUNDRE = 2000
 */
@Service
public class DatoFraIdentService {

    public LocalDate getFoedselsdato(String ident) {

        int year = parseInt(ident.substring(4, 6));
        int individ = parseInt(ident.substring(6, 9));

        // Find century
        int century;
        if (parseInt(ident.substring(6, 10)) == 0) {
            century = year <= LocalDate.now().getYear() % 100 ? 2000 : 1900;
        } else if (individ < 500 || (individ >= 900 && year > 39)) {
            century = 1900;
        } else if (individ >= 500 && year < 40) {
            century = 2000;
        } else if (individ >= 500 && individ < 750 && year > 54) {
            century = 1800;
        } else {
            century = 2000;
        }

        return LocalDate.of(century + year, getMonth(ident), getDay(ident));
    }

    private int getDay(String ident) {
        // Fix D-number
        return ident.charAt(0) >= '4' ? parseInt(ident.substring(0, 2)) - 40 :
                parseInt(ident.substring(0, 2));
    }

    private int getMonth(String ident) {
        // Fix B-number
        return ident.charAt(2) >= '2' ? parseInt(ident.substring(2, 4)) - 20 :
                parseInt(ident.substring(2, 4));
    }
}