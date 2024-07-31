package no.nav.levendearbeidsforholdscheduler.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.levendearbeidsforholdscheduler.consumer.AnsettelseConsumer;
import no.nav.levendearbeidsforholdscheduler.consumer.command.AnsettelseCommand;
import no.nav.levendearbeidsforholdscheduler.consumer.command.AnsettelsesCommand2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static no.nav.levendearbeidsforholdscheduler.utils.Utils.hentKalender;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobbScheduler {

    private final AnsettelseCommand ansettelseCommand;
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
        return scheduledFuture != null;
    }

    /**
     * Henter ut dato og tid for neste gang scheduleren skal kjøre en jobb
     * @return tidspunktet, då lenge det er en scheduler som er aktiv for øyeblikket
     */
    public Optional<String> hentTidspunktNesteKjoring(){

        if (scheduledFuture != null) {
            long delayIMillisekunder = scheduledFuture.getDelay(TimeUnit.MILLISECONDS);
            long scheduledTid = System.currentTimeMillis() + delayIMillisekunder;
            Date datoForNesteKjoring = new Date(scheduledTid);
            return Optional.of(datoForNesteKjoring.toString());
        } else {
            return Optional.empty();
        }

    }

    /**
     * Avslutter eventuelt nåværende schedule og starter en ny med det nye intervallet.
     * @param intervall Positivt heltall som representerer times-intervall for scheduler
     */
    public void startScheduler(long intervall){

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        log.info("Er i startScheduler");
        scheduledFuture = taskScheduler.scheduleAtFixedRate(new AnsettelseJobb(), INITIELL_FORSINKELSE, intervall, TimeUnit.HOURS);

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
    public static boolean gyldigTidsrom(int startKlokka, int startDag, int sluttKlokka, int sluttDag, int klokkeslett, int idag){
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
    public boolean sjekkOmGyldigTidsrom(int startKlokka, int startDag, int sluttKlokka, int sluttDag){

        Calendar kalender = hentKalender();
        int klokkeslett = kalender.get(Calendar.HOUR_OF_DAY); //06:00 = 6, 13:00 = 13, 23:00 = 23
        int dag = ((kalender.get(Calendar.DAY_OF_WEEK) + 6) % 7); //Mandag = 1, Tirsag = 2 ... Søndag = 7

        return gyldigTidsrom(startKlokka, startDag, sluttKlokka, sluttDag, klokkeslett, dag);
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
            log.info("Er i run");
            if(sjekkOmGyldigTidsrom(START_KLOKKESLETT, START_DAG, SLUTT_KLOKKESLETT, SLUTT_DAG)){
                log.info("Gyldig tidsrom kjører hent");

                ansettelsesService.hent();
                log.info("har kjørt hent kjører aktiver ansettelsecommand");

                //ansettelseCommand.aktiverAnsettelseService();

            }
            log.info("Kjørte jobb!");
        }
    }

}
