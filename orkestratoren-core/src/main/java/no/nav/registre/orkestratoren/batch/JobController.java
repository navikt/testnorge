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
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
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

    @Value("${orkestratoren.poppbatch.miljoe}")
    private String poppbatchMiljoe;

    @Value("${orkestratoren.batch.avspillergruppeId}")
    private Long avspillergruppeId;

    @Value("#{${orkestratoren.batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Value("${orkestratoren.eiabatch.antallSykemeldinger}")
    private int antallSykemeldinger;

    @Value("${orkestratoren.poppbatch.antallNyeIdenter}")
    private int poppbatchAntallNyeIdenter;

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Autowired
    private EiaSyntPakkenService eiaSyntPakkenService;

    @Autowired
    private PoppSyntPakkenService poppSyntPakkenService;

    @Scheduled(cron = "${orkestratoren.tpsbatch.cron:0 0 0 * * *}")
    public void tpsSyntBatch() {
        try {
            tpsSyntPakkenService.genererSkdmeldinger(avspillergruppeId, tpsbatchMiljoe, antallMeldingerPerEndringskode);
        } catch (HttpStatusCodeException e) {
            log.warn(e.getResponseBodyAsString(), e);
        }
    }

    @Scheduled(cron = "${orkestratoren.arenabatch.cron:0 0 1 1 * *}")
    public void arenaInntektSyntBatch() {
        SyntetiserInntektsmeldingRequest request = new SyntetiserInntektsmeldingRequest(avspillergruppeId);
        String arenaInntektId = arenaInntektSyntPakkenService.genererInntektsmeldinger(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger og mottat id {}.", arenaInntektId);
    }

    @Scheduled(cron = "${orkestratoren.eiabatch.cron:0 0 0 * * *}")
    public void eiaSyntBatch() {
        SyntetiserEiaRequest request = new SyntetiserEiaRequest(avspillergruppeId, eiabatchMiljoe, antallSykemeldinger);
        List<String> fnrMedGenererteMeldinger = eiaSyntPakkenService.genererEiaSykemeldinger(request);
        log.info("eiabatch har opprettet {} sykemeldinger. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), Arrays.toString(fnrMedGenererteMeldinger.toArray()));
    }

    @Scheduled(cron = "${orkestratoren.poppbatch.cron:0 0 1 6 * *}")
    public void poppSyntBatch() {
        SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, poppbatchMiljoe, poppbatchAntallNyeIdenter);
        String testdataEier = "orkestratoren";
        poppSyntPakkenService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
    }
}
