package no.nav.levendearbeidsforholdscheduler.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

public class Utils {

    /**
     * Formatterer en cron-expression for å kjøre en jobb hver x. time
     * @param intervallet Heltall som representerer antall timer forsinkelse
     * @return Ferdig formattert og gyldig cron-expression
     */
    public static String lagCronExpression(String intervallet) {
        return "0 0 */" + intervallet + " ? * MON-SAT";
    }

    /**
     * Funksjon som validerer om intervall er et positivt heltall
     * @param intervall Tekst med siffer som representerer antall timer forsinkelse for job-scheduleren
     * @return intervallet som heltall dersom det er et gyldig heltall
     */
    public static Optional<Long> sifferTilHeltall(String intervall) {

        try {
            long tall = Integer.parseInt(intervall);
            return Optional.of(tall);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Calendar hentKalender(){
        Date dato = new Date();
        Calendar kalender = GregorianCalendar.getInstance();
        kalender.setTime(dato);
        return kalender;
    }
}
