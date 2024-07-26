package no.nav.levendearbeidsforholdscheduler.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobbScheduler {

    @Autowired
    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private static final int START_KLOKKESLETT = 6;
    private static final int START_DAG = 1;

    private static final int SLUTT_KLOKKESLETT = 12;
    private static final int SLUTT_DAG = 6;

    /**
     * Avslutter eventuelt nåværende schedule og starter en ny med det nye intervallet.
     * @param cronExpression Gyldig cron-expression (forutsetter at expression er allerede formattert)
     */
    public void startScheduler(String cronExpression){

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        scheduledFuture = taskScheduler.schedule(new AnsettelseJobb(), new CronTrigger(cronExpression));
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

        Date dato = new Date();
        Calendar kalender = GregorianCalendar.getInstance();
        kalender.setTime(dato);
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
            if(sjekkOmGyldigTidsrom(START_KLOKKESLETT, START_DAG, SLUTT_KLOKKESLETT, SLUTT_DAG)){
                //Kall på consumer som sender request til levende-arbeidsforhold-ansettelse app
            }
            log.info("Kjørte jobb!");
        }
    }

}
