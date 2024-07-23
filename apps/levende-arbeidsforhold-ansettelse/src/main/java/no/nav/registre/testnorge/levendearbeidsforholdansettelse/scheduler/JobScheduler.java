package no.nav.registre.testnorge.levendearbeidsforholdansettelse.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobScheduler {

    private final JobbService jobbService;

    @Autowired
    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    /**
     * Funksjon som henter ut en intervallet fra databasen.
     * Funksjonen kalles når appen kjøres opp og vil kun ha en effekt dersom intervallet eksisterer i databasen.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void scheduleTask(){
        //Hent ut intervall fra databasen, eller sett default-verdi
        log.info("Alle parametere: {}", jobbService.hentAlleParametere().toString());

        List<JobbParameterEntity> parametere = jobbService.hentAlleParametere();


        parametere.forEach(param -> {
            if(param.getNavn().equals("intervall")){
                String intervall = param.getVerdi();
                log.info("Parameter-verdi for intervall: {}", intervall);
                rescheduleTask(intervall);
            }
        });
    }

    /**
     * Avslutter nåværende schedule og starter en ny med det nye intervallet.
     * Forutsetning at parameter allerede er på riktig cron-expression format.
     * @param intervall Heltall som representerer antall timer forsinkelse
     */
    public void rescheduleTask(String intervall){

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        //TODO: Sjekk om intervallet er positivt heltall før vi lager cron-expression osv
        String cronExpression = lagCronExpression(intervall);
        scheduledFuture = taskScheduler.schedule(new Job(), new CronTrigger(cronExpression));
        log.info("Schedulet en task med intervall: {}", cronExpression);

    }


    private static class Job implements Runnable {

        /**
         * Funksjon som automatisk blir kalt på av en task-scheduler og kjører ansettelsesservice utenom lørdag fra
         * klokken 12:00 til mandag klokken 06:00
         */
        @Override
        public void run() {

            /*
            if (!((dag == mandag && clock <= 6AM) || (dag == lørdag && clock >= 12PM)) ){
                //Kall på AnsettelseService her

            }
            */
            log.info("Jobb kjørte!");
        }
    }

    /**
     * Formatterer en cron-expression for å kjøre en jobb hver x. time
     * @param intervallet Heltall som representerer antall timer forsinkelse
     * @return Ferdig formattert og gyldig cron-expression
     */
    private String lagCronExpression(String intervallet) {
        return "0 0 */" + intervallet + " ? * MON-SAT";
    }
}
