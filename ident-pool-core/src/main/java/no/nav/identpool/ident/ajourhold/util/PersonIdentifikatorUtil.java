package no.nav.identpool.ident.ajourhold.util;

import java.time.LocalDate;

public class PersonIdentifikatorUtil {

    private PersonIdentifikatorUtil() {}

    public static LocalDate toBirthdate(String personIdentigikator) {
        int dag = Integer.parseInt(personIdentigikator.substring(0, 2));
        int maaned = Integer.parseInt(personIdentigikator.substring(2, 4));
        String aar = personIdentigikator.substring(4, 6);
        int epoke = Integer.parseInt(personIdentigikator.substring(6, 9));
        if (epoke > 0 && epoke < 500) {
            aar = "19" + aar;
        } else {
            aar = "20" + aar;
        }
        return LocalDate.of(Integer.parseInt(aar), maaned, dag);
    }
}
