package no.nav.registre.orkestratoren;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class JobController {

    @Scheduled(cron = "${orkestratoren.cron:0 0 4 * * *}")
    public void execute() {

    }
}
