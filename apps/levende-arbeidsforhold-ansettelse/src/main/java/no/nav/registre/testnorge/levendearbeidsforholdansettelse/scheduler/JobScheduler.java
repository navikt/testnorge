package no.nav.registre.testnorge.levendearbeidsforholdansettelse.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@EnableScheduling
public class JobScheduler {

    @Autowired
    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    public JobScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Funksjon som henter ut en intervallet (Som skal være en verdi med gyldig cron-expression) fra databasen.
     * Funksjonen kalles når appen kjøres opp.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void scheduleTask(){
        //Hent ut intervall fra databasen, eller sett default-verdi

        log.info("Schedulet en task!");
        rescheduleTask("0 * * ? * MON-FRI");
    }

    /**
     * Avslutter nåværende schedule og starter en ny med det nye intervallet.
     * Forutsetning at parameter allerede er på riktig cron-expression format.
     * @param cronExpression Det nye intervallet
     */
    public void rescheduleTask(String cronExpression){
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduledFuture = taskScheduler.schedule(new Job(), new CronTrigger(cronExpression));
    }

    private static class Job implements Runnable {

        @Override
        public void run() {
            //Kall på AnsettelseService her
            log.info("Jobb kjørte! Holder på å ansette folk nå!");
        }
    }
}
