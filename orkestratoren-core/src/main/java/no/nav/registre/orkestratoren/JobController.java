package no.nav.registre.orkestratoren;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.registre.orkestratoren.service.ConsumeTpsSyntPakken;

@Component
@EnableScheduling
public class JobController {
    
    @Value( "${orkestratoren.batch.miljoer}")
    private List<String> miljoer;
    @Value( "${orkestratoren.batch.opprettAntallPersoner:500}")
    private int antallPersoner;
    
    @Autowired
    private ConsumeTpsSyntPakken consumeTpsSyntPakken;

    @Scheduled(cron = "${orkestratoren.cron:0 0 4 * * *}")
    public void execute() {
        consumeTpsSyntPakken.produserOgSendSkdmeldingerTilTpsIMiljoer(miljoer, antallPersoner);
    }
}
