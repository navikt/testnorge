package no.nav.registre.testnorge.levendearbeidsforholdansettelse.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

@Slf4j
public class JobScheduler {

    public void rescheduleTask(String cronExpression){

    }

    private class Job implements Runnable {

        @Override
        public void run() {
            //Kall på AnsettelseService her
            log.info("Jobb kjørte!");
        }
    }
}
