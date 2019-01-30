package no.nav.registre.orkestratoren.batch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@Component
@EnableScheduling
@Getter
@Slf4j
public class JobController {

    @Value("${orkestratoren.tpsbatch.miljoe}")
    private String tpsbatchMiljoe;

    @Value("${orkestratoren.eiabatch.miljoe}")
    private String eiabatchMiljoe;

    private String arenaInntektMiljoe;

    @Value("${orkestratoren.batch.avspillergruppeId}")
    private Long avspillergruppeId;

    @Value("#{${orkestratoren.batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Value("${orkestratoren.eiabatch.antallSykemeldinger}")
    private int antallSykemeldinger;

    private int antallArenaInntektPersoner;

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Autowired
    private EiaSyntPakkenService eiaSyntPakkenService;

    @Scheduled(cron = "${orkestratoren.tpsbatch.cron:0 0 0 * * *}")
    public void tpsSyntBatch() {
        try {
            tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(avspillergruppeId, tpsbatchMiljoe, antallMeldingerPerEndringskode);
        } catch (HttpStatusCodeException e) {
            log.warn(e.getResponseBodyAsString(), e);
        }
    }

    @Scheduled(cron = "${orkestratoren.arenabatch.cron:0 0 1 1 * *}")
    public void arenaInntektSyntBatch() {
        SyntetiserInntektsmeldingRequest request = new SyntetiserInntektsmeldingRequest(avspillergruppeId);
        String arenaInntektId = arenaInntektSyntPakkenService.genererEnInntektsmeldingPerFnrIInntektstub(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger og mottat id {}.", arenaInntektId);
    }

    @Scheduled(cron = "${orkestratoren.eiabatch.cron:0 0 0 * * *}")
    public void eiaSyntBatch() {
        SyntetiserEiaRequest request = new SyntetiserEiaRequest(avspillergruppeId, eiabatchMiljoe, antallSykemeldinger);
        List<String> fnrMedGenererteMeldinger = eiaSyntPakkenService.genererEiaSykemeldinger(request);
        log.info("eiabatch har opprettet {} sykemeldinger. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), Arrays.toString(fnrMedGenererteMeldinger.toArray()));
    }
}
