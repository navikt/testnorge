package no.nav.registre.testnorge.levendearbeidsforholdansettelse.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.AnsettelseService;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobbScheduler {

    private final JobbService jobbService;

    @Autowired
    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private static final String DEFAULT_INTERVALL = "24";
    private static final String INTERVALL_PARAM_NAVN = "intervall";

    private static final int START_KLOKKESLETT = 6;
    private static final int START_DAG = 1;

    private static final int SLUTT_KLOKKESLETT = 12;
    private static final int SLUTT_DAG = 6;
    
    @Autowired
    private AnsettelseService ansettelseService;

    /**
     * Funksjon som henter ut en intervallet fra databasen
     */
    //@EventListener(ApplicationReadyEvent.class)
    public void scheduleMedIntervallFraDb(){
        //Hent ut intervall fra databasen, eller sett default-verdi
        log.info("Alle parametere: {}", jobbService.hentAlleParametere().toString());

        List<JobbParameterEntity> parametere = jobbService.hentAlleParametere();

        parametere.forEach(param -> {
            if(param.getNavn().equals(INTERVALL_PARAM_NAVN)){
                String intervall = param.getVerdi();
                log.info("Parameter-verdi for intervall: {}", intervall);
                startScheduler(intervall);
            }
        });
    }

    /**
     * Avslutter eventuelt nåværende schedule og starter en ny med det nye intervallet.
     * @param intervall Heltall som representerer antall timer forsinkelse for job-scheduleren
     */
    public void startScheduler(String intervall){

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        String cronExpression;
        if (intervallErHeltall(intervall)){
            cronExpression = lagCronExpression(intervall);
        } else {
            cronExpression = lagCronExpression(DEFAULT_INTERVALL);
        }

        scheduledFuture = taskScheduler.schedule(new AnsettelseJobb(), new CronTrigger(cronExpression));
        log.info("Schedulet en task med intervall: {}", cronExpression);

    }

    /**
     * Formatterer en cron-expression for å kjøre en jobb hver x. time
     * @param intervallet Heltall som representerer antall timer forsinkelse
     * @return Ferdig formattert og gyldig cron-expression
     */
    private String lagCronExpression(String intervallet) {
        return "0 0 */" + intervallet + " ? * MON-SAT";
    }

    /**
     * Funksjon som validerer om intervall er et positivt heltall. Brukes kun i this.rescheduleTask() metoden.
     * @param intervall Heltall som representerer antall timer forsinkelse for job-scheduleren
     * @return true hvis intervallet er et positivt heltall og false hvis ikke
     */
    private static boolean intervallErHeltall(String intervall) {
        try {
            Integer.parseInt(intervall);
        } catch (NumberFormatException e) {
            return false;
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
     * @return true hvis idag og klokkeslett er innenfor det gitte tidsrommet i en vilkårlig uke
     */
    public static boolean gyldigTidsrom(int startKlokka, int startDag, int sluttKlokka, int sluttDag, int klokkeslett, int idag){
        return (startDag > sluttDag && (idag > startDag || idag < sluttDag)) //Ved ukes-skifte
                || (idag > startDag && idag < sluttDag) //Innad i samme uke
                || ((idag == startDag && klokkeslett >= startKlokka) //Samme dag, startdag
                || (idag == sluttDag && klokkeslett < sluttKlokka)); //Samme dag, sluttdag
    }


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
                ansettelseService.runAnsettelseService();
            }
        }
    }

}
