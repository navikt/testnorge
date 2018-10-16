package no.nav.registre.orkestratoren;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.registre.orkestratoren.service.TpsSyntPakkenConsumer;

@Component
@EnableScheduling
public class JobController {

    @Value("${orkestratoren.batch.miljoer}.split(',')")
    private List<String> miljoer;

    @Value("${orkestratoren.batch.antallSkdMeldinger:500}")
    private int antallSkdMeldinger;

    @Value("${orkestratoren.batch.aarsakskoder}.split(',')")
    private List<String> aarsakskoder;

    @Autowired
    private TpsSyntPakkenConsumer tpsSyntPakkenConsumer;

    @Scheduled(cron = "${orkestratoren.cron:0 0 4 * * *}")
    public void execute() {
        tpsSyntPakkenConsumer.produserOgSendSkdmeldingerTilTpsIMiljoer(antallSkdMeldinger, miljoer, aarsakskoder);
    }
}
