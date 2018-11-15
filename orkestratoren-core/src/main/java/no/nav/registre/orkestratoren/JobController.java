package no.nav.registre.orkestratoren;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenConsumer;

@Component
@EnableScheduling
@Getter
public class JobController {

    @Value("${orkestratoren.batch.miljoe}")
    private String miljoe;

    @Value("${orkestratoren.batch.skdMeldingGruppeId}")
    private Long skdMeldingGruppeId;

    @Value("#{${orkestratoren.batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Autowired
    private TpsSyntPakkenConsumer tpsSyntPakkenConsumer;

    @LogExceptions
    @Scheduled(cron = "${orkestratoren.batch.cron:0 0 * * * *}")
    public void tpsSyntBatch() {
        tpsSyntPakkenConsumer.produserOgSendSkdmeldingerTilTpsIMiljoer(skdMeldingGruppeId, miljoe, antallMeldingerPerEndringskode);
    }
}
