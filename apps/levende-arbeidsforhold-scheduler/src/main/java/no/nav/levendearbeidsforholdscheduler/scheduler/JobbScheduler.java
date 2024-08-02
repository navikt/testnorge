package no.nav.levendearbeidsforholdscheduler.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static no.nav.levendearbeidsforholdscheduler.utils.Utils.beregnEkstraDelayNaa;
import static no.nav.levendearbeidsforholdscheduler.utils.Utils.hentKalender;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobbScheduler {

    private final AnsettelsesService ansettelsesService;
    @Autowired
    private final ScheduledExecutorService taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private static final int START_KLOKKESLETT = 6;
    private static final int START_DAG = 1;

    private static final int SLUTT_KLOKKESLETT = 12;
    private static final int SLUTT_DAG = 6;

    private static final long INITIELL_FORSINKELSE = 0;

    /**
     * Funksjon som brukes for å sjekke om scheduleren kjører for øyeblikket
     * @return true hvis scheduler kjører for øyeblikket
     */
    public boolean hentStatusKjorer(){
        if (scheduledFuture != null) {

            return !(scheduledFuture.state() == Future.State.CANCELLED);
        } else {
            return false;
        }
    }

    /**
     * Henter ut dato og tid for neste gang scheduleren skal kjøre en jobb
     * @return tidspunktet, då lenge det er en scheduler som er aktiv for øyeblikket
     */
    public Optional<String> hentTidspunktNesteKjoring(){

        if (scheduledFuture != null) {
            long delayIMillisekunder = scheduledFuture.getDelay(TimeUnit.MILLISECONDS);
            long nesteKjoringMillisekunder = beregnNesteKjoring(delayIMillisekunder);

            Date datoForNesteKjoring = new Date(nesteKjoringMillisekunder);
            return Optional.of(datoForNesteKjoring.toString());
        } else {
            return Optional.empty();
        }

    }

    /**
     * Avslutter eventuelt nåværende schedule og starter en ny med det nye intervallet.
     * @param intervall Positivt heltall som representerer times-intervall for scheduler
     * @return true hvis aktivering av scheduler med ansettelse-jobb var vellykket
     */
    public boolean startScheduler(long intervall){

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        scheduledFuture = taskScheduler.scheduleAtFixedRate(new AnsettelseJobb(), INITIELL_FORSINKELSE, intervall, TimeUnit.HOURS);

        return scheduledFuture.state() == Future.State.RUNNING;
    }


    /**
     * Funksjon som stopper den nåværende/kjørende jobben
     * @return true hvis jobben ble stoppet vellykket
     */
    public boolean stoppScheduler(){
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);

            return scheduledFuture.isCancelled();
        }
        return true;
    }

    /**
     * Funksjon som validerer om angitt tidspunkt er innenfor et gitt tidsrom for en vilkårlig uke.
     * Brukes i forbindelse med å ikke kjøre AnsettelseJobb-klassen på gitte-tidspunkter i helgen for eksempel.
     * @param startKlokka Tall som representerer timen i en 24-timers klokke for klokkeslett på start for gyldig tidsrom
     * @param startDag Tall som representerer dag i uken fra 1-7 (man-søn) for start på gyldig tidsrom
     * @param sluttKlokka Tall som representerer timen i en 24-timers klokke for klokkeslett på start for gyldig tidsrom
     * @param sluttDag Tall som representerer dag i uken fra 1-7 (man-søn) for start på gyldig tidsrom
     * @param klokkeslett Tall som representerer timen i en 24-timers klokke for nåværende tidspunkt
     * @param idag Tall som representerer dagen i uken i en 24-timers klokke
     * @return true hvis angitt dag og klokkeslett er innenfor det gitte tidsrommet i en vilkårlig uke
     */
    public static boolean sjekkGyldigTidsrom(int startKlokka, int startDag, int sluttKlokka, int sluttDag, int klokkeslett, int idag){
        return (startDag > sluttDag && (idag > startDag || idag < sluttDag)) //Ved ukes-skifte
                || (idag > startDag && idag < sluttDag) //Innad i samme uke
                || ((idag == startDag && klokkeslett >= startKlokka) //Samme dag, startdag
                || (idag == sluttDag && klokkeslett < sluttKlokka)); //Samme dag, sluttdag
    }


    /**
     * Funksjon som sjekker om nåværende tidspunkt med klokke-time og dag er innenfor gyldig tidsrom
     * @param startKlokka Tall som representerer timen i en 24-timers klokke for klokkeslett på start for gyldig tidsrom
     * @param startDag Tall som representerer dag i uken fra 1-7 (man-søn) for start på gyldig tidsrom
     * @param sluttKlokka Tall som representerer timen i en 24-timers klokke for klokkeslett på start for gyldig tidsrom
     * @param sluttDag Tall som representerer dag i uken fra 1-7 (man-søn) for start på gyldig tidsrom
     * @return true hvis nåværende dag og klokkeslett er innenfor det gitte tidsrommet i en vilkårlig uke
     */
    public boolean sjekkOmGyldigTidsromNaa(int startKlokka, int startDag, int sluttKlokka, int sluttDag){

        Calendar kalender = hentKalender();
        int klokkeslett = kalender.get(Calendar.HOUR_OF_DAY); //06:00 = 6, 13:00 = 13, 23:00 = 23
        int dag = ((kalender.get(Calendar.DAY_OF_WEEK) + 6) % 7); //Mandag = 1, Tirsag = 2 ... Søndag = 7

        return sjekkGyldigTidsrom(startKlokka, startDag, sluttKlokka, sluttDag, klokkeslett, dag);
    }

    /**
     * Funksjon som beregner timestamp/tidspunkt (med millisekunder som tidsenhet) for neste kjøring av nåværende
     * jobb
     * @param schedulerDelay Delay/forsinkelse scheduler har for kjøring av jobb
     * @return positivt heltall som representerer tidspunktet for neste kjøring i millisekunder
     */
    public long beregnNesteKjoring(long schedulerDelay){
        long nesteKjoringMillisekunder = System.currentTimeMillis() + schedulerDelay;

        if (!sjekkOmGyldigTidsromNaa(START_KLOKKESLETT, START_DAG, SLUTT_KLOKKESLETT, SLUTT_DAG)){
            nesteKjoringMillisekunder = System.currentTimeMillis() + beregnEkstraDelayNaa(START_DAG, START_KLOKKESLETT);
        }
        return nesteKjoringMillisekunder;
    }

    /**
     * Klasse for jobben som skal kjøres av scheduler
     */
    private class AnsettelseJobb implements Runnable {

        /**
         * Funksjon som automatisk blir kalt på av en task-scheduler og kjører ansettelsesservice innenfor gyldig
         * tidsrom
         */
        @Override
        public void run() {
            if(sjekkOmGyldigTidsromNaa(START_KLOKKESLETT, START_DAG, SLUTT_KLOKKESLETT, SLUTT_DAG)){
                ansettelsesService.hent();
            }

        }
    }

}
