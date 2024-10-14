package no.nav.testnav.levendearbeidsforholdscheduler.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static java.lang.Math.abs;

public class Utils {

    private static final int DAGER_I_UKA = 7;

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

    public static long timerTilMillisek(long antTimer){
        return antTimer * 60 * 60 * 1000;
    }

    public static long dagerTilMillisek(long antDager){
        return antDager * timerTilMillisek(24);
    }

    /**
     * Henter ekstra forsinkelse for uthenting av tidspunkt for neste kjøring av scheduler for nåværende tidspunkt.
     * Brukes når status på scheduler hentes i ugyldig tidsrom
     * @param startdag Positivt heltall fra 1-7 som representerer første dag i uka for gyldig tidsrom
     * @param startklokkeslett Positivt heltall fra 0-23 som representerer start-klokkeslett for gyldig tidsrom
     * @return Delay i antall millisekunder for nåværende tidspunkt
     */
    public static long beregnEkstraDelayNaa(int startdag, int startklokkeslett){
        Calendar kalender = hentKalender();
        int klokkeslett = kalender.get(Calendar.HOUR_OF_DAY); //06:00 = 6, 13:00 = 13, 23:00 = 23
        int dag = ((kalender.get(Calendar.DAY_OF_WEEK) + 6) % 7); //Mandag = 1, Tirsag = 2 ... Søndag = 7

        return beregnEkstraDelay(startdag, startklokkeslett, klokkeslett, dag);
    }

    /**
     * Henter ekstra forsinkelse for uthenting av tidspunkt for neste kjøring av scheduler.
     * @param startdag Positivt heltall fra 1-7 som representerer første dag i uka for gyldig tidsrom
     * @param startklokkeslett Positivt heltall fra 0-23 som representerer start-klokkeslett for gyldig tidsrom
     * @param klokkeslett Positivt heltall fra 0-23 som representerer nåværende klokkeslett
     * @param dag Positivt heltall fra 1-7 som representerer dag i uka for nåværende tidspunkt
     * @return Delay i antall millisekunder
     */
    public static long beregnEkstraDelay(int startdag, int startklokkeslett, int klokkeslett, int dag){
        int antallDagerTilKjoring = 0;
        if(startdag < dag){
            antallDagerTilKjoring = DAGER_I_UKA - abs(startdag - dag);
        } else {
            antallDagerTilKjoring = startdag - dag;
        }

        int antallTimer = startklokkeslett - klokkeslett;

        long milliSekunderTimer = timerTilMillisek(antallTimer);
        long milliSekunderDager = dagerTilMillisek(antallDagerTilKjoring);

        return milliSekunderTimer + milliSekunderDager;
    }
}
