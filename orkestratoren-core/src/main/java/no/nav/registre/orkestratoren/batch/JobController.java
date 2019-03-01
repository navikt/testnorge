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

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.service.AaregSyntPakkenService;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@Component
@EnableScheduling
@Getter
@Slf4j
public class JobController {

    @Value("#{'${orkestratoren.tpsbatch.miljoe}'.split(', ')}")
    private List<String> tpsbatchMiljoe;

    @Value("#{'${orkestratoren.eiabatch.miljoe}'.split(', ')}")
    private List<String> eiabatchMiljoe;

    @Value("#{'${orkestratoren.poppbatch.miljoe}'.split(', ')}")
    private List<String> poppbatchMiljoe;

    @Value("#{'${orkestratoren.aaregbatch.miljoe}'.split(', ')}")
    private List<String> aaregbatchMiljoe;

    @Value("${orkestratoren.batch.avspillergruppeId}")
    private Long avspillergruppeId;

    @Value("#{${orkestratoren.batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Value("${orkestratoren.eiabatch.antallSykemeldinger}")
    private int antallSykemeldinger;

    @Value("${orkestratoren.poppbatch.antallNyeIdenter}")
    private int poppbatchAntallNyeIdenter;

    @Value("${orkestratoren.aaregbatch.antallNyeIdenter}")
    private int aaregbatchAntallNyeIdenter;

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Autowired
    private EiaSyntPakkenService eiaSyntPakkenService;

    @Autowired
    private PoppSyntPakkenService poppSyntPakkenService;

    @Autowired
    private AaregSyntPakkenService aaregSyntPakkenService;

    @Scheduled(cron = "${orkestratoren.tpsbatch.cron:0 0 0 * * *}")
    public void tpsSyntBatch() {
        for(String miljoe : tpsbatchMiljoe) {
            try {
                tpsSyntPakkenService.genererSkdmeldinger(avspillergruppeId, miljoe, antallMeldingerPerEndringskode);
            } catch (HttpStatusCodeException e) {
                log.warn(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Scheduled(cron = "${orkestratoren.inntektbatch.cron:0 0 1 1 * *}")
    public void arenaInntektSyntBatch() {
        SyntetiserInntektsmeldingRequest request = new SyntetiserInntektsmeldingRequest(avspillergruppeId);
        String arenaInntektId = arenaInntektSyntPakkenService.genererInntektsmeldinger(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger og mottat id {}.", arenaInntektId);
    }

    @Scheduled(cron = "${orkestratoren.eiabatch.cron:0 0 0 * * *}")
    public void eiaSyntBatch() {
        for(String miljoe : eiabatchMiljoe) {
            SyntetiserEiaRequest request = new SyntetiserEiaRequest(avspillergruppeId, miljoe, antallSykemeldinger);
            List<String> fnrMedGenererteMeldinger = eiaSyntPakkenService.genererEiaSykemeldinger(request);
            log.info("eiabatch har opprettet {} sykemeldinger i miljø {}. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), miljoe, Arrays.toString(fnrMedGenererteMeldinger.toArray()));
        }
    }

    @Scheduled(cron = "${orkestratoren.poppbatch.cron:0 0 1 1 5 *}")
    public void poppSyntBatch() {
        for(String miljoe : poppbatchMiljoe) {
            SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, poppbatchAntallNyeIdenter);
            String testdataEier = "orkestratoren";
            poppSyntPakkenService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
        }
    }

    @Scheduled(cron = "${orkestratoren.aaregbatch.cron:0 0 1 1 * *}")
    public void aaregSyntBatch() {
        for(String miljoe : aaregbatchMiljoe) {
            SyntetiserAaregRequest syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, aaregbatchAntallNyeIdenter);
            aaregSyntPakkenService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest);
        }
    }
}
