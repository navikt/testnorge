package no.nav.registre.orkestratoren.batch.v1;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaVedtakshistorikkRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Map;

import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeFrikortService;
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

    @Value("${poppbatch.antallNyeIdenter}")
    private int poppbatchAntallNyeIdenter;

    @Value("${aaregbatch.antallNyeIdenter}")
    private int aaregbatchAntallNyeIdenter;

    @Value("${instbatch.antallNyeIdenter}")
    private int instbatchAntallNyeIdenter;

    @Value("${bisysbatch.antallNyeIdenter}")
    private int bisysbatchAntallNyeIdenter;

    @Value("${tpbatch.antallPersoner}")
    private int tpAntallPersoner;

    @Value("${sambatch.antallMeldinger}")
    private int samAntallMeldinger;

    @Value("${arenabatch.antallNyeIdenter}")
    private int arenaAntallNyeIdenter;

    @Value("${medlbatch.prosentfaktor}")
    private double medlProsentfaktor;

    @Value("${frikortbatch.antallNyeIdenter}")
    private int frikortAntallNyeIdenter;

    @Autowired
    private TestnorgeSkdService testnorgeSkdService;

    @Autowired
    private TestnorgeInntektService testnorgeInntektService;

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
    private TestnorgeArenaService testnorgeArenaService;

    @Autowired
    private TestnorgeMedlService testnorgeMedlService;

    @Autowired
    private TestnorgeFrikortService testnorgeFrikortService;

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
        log.info("Nav-endringsmeldinger batch midlertidig stanset.");
//        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
//            var syntetiserNavmeldingerRequest = SyntetiserNavmeldingerRequest.builder()
//                    .avspillergruppeId(entry.getKey())
//                    .miljoe(entry.getValue())
//                    .antallMeldingerPerEndringskode(antallNavmeldingerPerEndringskode)
//                    .build();
//            testnorgeSkdService.genererNavmeldinger(syntetiserNavmeldingerRequest);
//        }
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void inntektSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var request = new SyntetiserInntektsmeldingRequest(entry.getKey(), entry.getValue());
            var feiledeInntektsmeldinger = testnorgeInntektService.genererInntektsmeldinger(request);
            log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger. Meldinger som feilet: {}.", feiledeInntektsmeldinger.keySet().toString());
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
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserInstRequest = new SyntetiserInstRequest(entry.getKey(), entry.getValue(), instbatchAntallNyeIdenter);
            testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
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

    /**
     * Denne metoden oppretter vedtakshistorikk i Arena og registerer brukere med oppfølging (uten vedtak) i Arena.
     * Metoden kjøres hver time.
     */
    @Scheduled(cron = "0 0 0-23 * * *")
    public void arenaSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            for (int i = 0; i < 4; i++) {
                testnorgeArenaService.opprettArenaVedtakshistorikk(SyntetiserArenaVedtakshistorikkRequest.builder()
                        .avspillergruppeId(entry.getKey())
                        .miljoe(entry.getValue())
                        .antallVedtakshistorikker(arenaAntallNyeIdenter)
                        .build());

                testnorgeArenaService.opprettArbeidssokereIArena(SyntetiserArenaRequest.builder()
                        .avspillergruppeId(entry.getKey())
                        .miljoe(entry.getValue())
                        .antallNyeIdenter(1)
                        .build(), true);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void medlSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserMedlRequest = new SyntetiserMedlRequest(entry.getKey(), entry.getValue(), medlProsentfaktor);
            testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void frikortSyntBatch() {
        for (var entry : avspillergruppeIdMedMiljoe.entrySet()) {
            var syntetiserFrikortRequest = new SyntetiserFrikortRequest(entry.getKey(), entry.getValue(), frikortAntallNyeIdenter);
            testnorgeFrikortService.genererFrikortEgenmeldinger(syntetiserFrikortRequest);
        }
    }
}
