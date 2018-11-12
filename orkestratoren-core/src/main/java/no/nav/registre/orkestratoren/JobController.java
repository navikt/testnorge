package no.nav.registre.orkestratoren;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenConsumer;

@Component
@EnableScheduling
public class JobController {

    @Value("${orkestratoren.batch.miljoe:t9}")
    private String miljoe;

    @Value("${orkestratoren.batch.skdMeldingGruppeId:500}")
    private Long skdMeldingGruppeId;

    @Autowired
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Autowired
    private TpsSyntPakkenConsumer tpsSyntPakkenConsumer;

    @LogExceptions
    @Scheduled(cron = "${orkestratoren.cron:0 0 4 * * *}")
    public void execute() {
        tpsSyntPakkenConsumer.produserOgSendSkdmeldingerTilTpsIMiljoer(skdMeldingGruppeId, miljoe, antallMeldingerPerEndringskode);
    }
}
