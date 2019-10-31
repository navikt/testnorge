package no.nav.registre.orkestratoren.batch;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TesnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeEiaService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;
import no.nav.registre.orkestratoren.service.TestnorgeSkdService;

@Component
@EnableScheduling
@Getter
@Slf4j
public class JobController {

    @Value("#{${batch.avspillergruppeId.miljoe}}")
    private Map<Long, String> avspillergruppeIdMedMiljoe;

    @Value("#{${batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallSkdmeldingerPerEndringskode;

    @Value("#{${batch.navMeldinger}}")
    private Map<String, Integer> antallNavmeldingerPerEndringskode;

    @Value("${eiabatch.antallSykemeldinger}")
    private int antallSykemeldinger;

    @Value("${poppbatch.antallNyeIdenter}")
    private int poppbatchAntallNyeIdenter;

    @Value("${inntektbatch.avspillergruppeId}")
    private Long inntektbatchAvspillergruppeId;

    @Value("${aaregbatch.antallNyeIdenter}")
    private int aaregbatchAntallNyeIdenter;

    @Value("#{'${instbatch.miljoe}'.split(', ')}")
    private List<String> instbatchMiljoe;

    @Value("${instbatch.avspillergruppeId}")
    private Long instbatchAvspillergruppeId;

    @Value("${instbatch.antallNyeIdenter}")
    private int instbatchAntallNyeIdenter;

    @Value("${bisysbatch.antallNyeIdenter}")
    private int bisysbatchAntallNyeIdenter;

    @Value("${tpbatch.antallPersoner}")
    private int tpAntallPersoner;

    @Value("${sambatch.antallMeldinger}")
    private int samAntallMeldinger;

    @Value("${arenabatch.antallNyeIdenter}")
    private int arenaAnallNyeIdenter;

    @Value("${medlbatch.prosentfaktor}")
    private double medlProsentfaktor;

    @Autowired
    private TestnorgeSkdService testnorgeSkdService;

    @Autowired
    private TestnorgeInntektService testnorgeInntektService;

    @Autowired
    private TestnorgeEiaService testnorgeEiaService;

    @Autowired
    private TestnorgeSigrunService testnorgeSigrunService;

    @Autowired
    private TestnorgeAaregService testnorgeAaregService;

    @Autowired
    private TestnorgeInstService testnorgeInstService;

    @Autowired
    private TestnorgeBisysService testnorgeBisysService;

    @Autowired
    private TestnorgeTpService testnorgeTpService;

    @Autowired
    private TestnorgeSamService testnorgeSamService;

    @Autowired
    private TesnorgeArenaService tesnorgeArenaService;

    @Autowired
    private TestnorgeMedlService testnorgeMedlService;

    @Scheduled(cron = "0 0 0 * * *")
    public void tpsSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            try {
                testnorgeSkdService.genererSkdmeldinger(entry.getKey(), entry.getValue(), antallSkdmeldingerPerEndringskode);
            } catch (HttpStatusCodeException e) {
                log.warn(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void navSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest = SyntetiserNavmeldingerRequest.builder()
                    .avspillergruppeId(entry.getKey())
                    .miljoe(entry.getValue())
                    .antallMeldingerPerEndringskode(antallNavmeldingerPerEndringskode)
                    .build();
            testnorgeSkdService.genererNavmeldinger(syntetiserNavmeldingerRequest);
        }
    }

//    @Scheduled(cron = "0 0 1 1 * *")
    public void inntektSyntBatch() {
        SyntetiserInntektsmeldingRequest request = new SyntetiserInntektsmeldingRequest(inntektbatchAvspillergruppeId);
        Map<String, List<Object>> feiledeInntektsmeldinger = testnorgeInntektService.genererInntektsmeldinger(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger. Meldinger som feilet: {}.", feiledeInntektsmeldinger.keySet().toString());
    }

    public void eiaSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserEiaRequest request = new SyntetiserEiaRequest(entry.getKey(), entry.getValue(), antallSykemeldinger);
            List<String> fnrMedGenererteMeldinger = testnorgeEiaService.genererEiaSykemeldinger(request);
            log.info("eiabatch har opprettet {} sykemeldinger i miljø {}. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), entry.getValue(),
                    Arrays.toString(fnrMedGenererteMeldinger.toArray()));
        }
    }

    @Scheduled(cron = "0 0 1 1 5 *")
    public void poppSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(entry.getKey(), entry.getValue(), poppbatchAntallNyeIdenter);
            String testdataEier = "synt_test";
            testnorgeSigrunService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
        }
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void aaregSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserAaregRequest syntetiserAaregRequest = new SyntetiserAaregRequest(entry.getKey(), entry.getValue(), aaregbatchAntallNyeIdenter);
            testnorgeAaregService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, true);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void instSyntBatch() {
        for (String miljoe : instbatchMiljoe) {
            SyntetiserInstRequest syntetiserInstRequest = new SyntetiserInstRequest(instbatchAvspillergruppeId, miljoe, instbatchAntallNyeIdenter);
            testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);
        }
    }

    public void bisysSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserBisysRequest syntetiserBisysRequest = new SyntetiserBisysRequest(entry.getKey(), entry.getValue(), bisysbatchAntallNyeIdenter);
            testnorgeBisysService.genererBistandsmeldinger(syntetiserBisysRequest);
        }
    }

    @Scheduled(cron = "0 0 0 1 5 *")
    public void tpSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserTpRequest request = new SyntetiserTpRequest(entry.getKey(), entry.getValue(), tpAntallPersoner);
            ResponseEntity entity = testnorgeTpService.genererTp(request);
            if (!entity.getStatusCode().is2xxSuccessful()) {
                log.error("Klarte ikke å fullføre syntetisering i TP batch kjøring");
            }
        }
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void samSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserSamRequest syntetiserSamRequest = new SyntetiserSamRequest(entry.getKey(), entry.getValue(), samAntallMeldinger);
            testnorgeSamService.genererSamordningsmeldinger(syntetiserSamRequest);
        }
    }

    public void arenaSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserArenaRequest syntetiserArenaRequest = new SyntetiserArenaRequest(entry.getKey(), entry.getValue(), arenaAnallNyeIdenter);
            tesnorgeArenaService.opprettArbeidssokereIArena(syntetiserArenaRequest);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void medlSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserMedlRequest syntetiserMedlRequest = new SyntetiserMedlRequest(entry.getKey(), entry.getValue(), medlProsentfaktor);
            testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);
        }
    }
}
