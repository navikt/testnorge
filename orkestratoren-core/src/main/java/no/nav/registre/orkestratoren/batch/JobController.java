package no.nav.registre.orkestratoren.batch;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserElsamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.TesnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeElsamService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeSkdService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;

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

    @Value("${elsambatch.antallSykemeldinger}")
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
    private TestnorgeElsamService testnorgeElsamService;

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
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            try {
                testnorgeSkdService.genererSkdmeldinger(entry.getKey(), entry.getValue(), antallSkdmeldingerPerEndringskode);
            } catch (HttpStatusCodeException e) {
                log.warn(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void navSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserNavmeldingerRequest = SyntetiserNavmeldingerRequest.builder()
                    .avspillergruppeId(entry.getKey())
                    .miljoe(entry.getValue())
                    .antallMeldingerPerEndringskode(antallNavmeldingerPerEndringskode)
                    .build();
            testnorgeSkdService.genererNavmeldinger(syntetiserNavmeldingerRequest);
        }
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void inntektSyntBatch() {
        var request = new SyntetiserInntektsmeldingRequest(inntektbatchAvspillergruppeId);
        var feiledeInntektsmeldinger = testnorgeInntektService.genererInntektsmeldinger(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger. Meldinger som feilet: {}.", feiledeInntektsmeldinger.keySet().toString());
    }

    public void elsamSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var request = new SyntetiserElsamRequest(entry.getKey(), entry.getValue(), antallSykemeldinger);
            var fnrMedGenererteMeldinger = testnorgeElsamService.genererElsamSykemeldinger(request);
            log.info("elsambatch har opprettet {} sykemeldinger i miljø {}. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), entry.getValue(),
                    Arrays.toString(fnrMedGenererteMeldinger.toArray()));
        }
    }

    @Scheduled(cron = "0 0 1 1 5 *")
    public void poppSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserPoppRequest = new SyntetiserPoppRequest(entry.getKey(), entry.getValue(), poppbatchAntallNyeIdenter);
            var testdataEier = "synt_test";
            testnorgeSigrunService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
        }
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void aaregSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserAaregRequest = new SyntetiserAaregRequest(entry.getKey(), entry.getValue(), aaregbatchAntallNyeIdenter);
            testnorgeAaregService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, true);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void instSyntBatch() {
        for (var miljoe : instbatchMiljoe) {
            var syntetiserInstRequest = new SyntetiserInstRequest(instbatchAvspillergruppeId, miljoe, instbatchAntallNyeIdenter);
            testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);
        }
    }

    public void bisysSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserBisysRequest = new SyntetiserBisysRequest(entry.getKey(), entry.getValue(), bisysbatchAntallNyeIdenter);
            testnorgeBisysService.genererBistandsmeldinger(syntetiserBisysRequest);
        }
    }

    @Scheduled(cron = "0 0 0 1 5 *")
    public void tpSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var request = new SyntetiserTpRequest(entry.getKey(), entry.getValue(), tpAntallPersoner);
            var entity = testnorgeTpService.genererTp(request);
            if (!entity.getStatusCode().is2xxSuccessful()) {
                log.error("Klarte ikke å fullføre syntetisering i TP batch kjøring");
            }
        }
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void samSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserSamRequest = new SyntetiserSamRequest(entry.getKey(), entry.getValue(), samAntallMeldinger);
            testnorgeSamService.genererSamordningsmeldinger(syntetiserSamRequest);
        }
    }

    public void arenaSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserArenaRequest = new SyntetiserArenaRequest(entry.getKey(), entry.getValue(), arenaAnallNyeIdenter);
            tesnorgeArenaService.opprettArbeidssokereIArena(syntetiserArenaRequest);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void medlSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserMedlRequest = new SyntetiserMedlRequest(entry.getKey(), entry.getValue(), medlProsentfaktor);
            testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);
        }
    }
}
