package no.nav.registre.orkestratoren.batch.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.ArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;

@Slf4j
@Getter
@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobController {

    @Value("${batch.avspillergruppeId}")
    private long avspillergruppeId;

    @Value("${batch.miljoe}")
    private String miljoe;

    @Value("${batch.popp.antallNyeIdenter}")
    private int poppbatchAntallNyeIdenter;

    @Value("${batch.aareg.antallNyeIdenter}")
    private int aaregbatchAntallNyeIdenter;

    @Value("${batch.inst.antallNyeIdenter}")
    private int instbatchAntallNyeIdenter;

    @Value("${batch.tp.antallPersoner}")
    private int tpAntallPersoner;

    @Value("${batch.sam.antallMeldinger}")
    private int samAntallMeldinger;

    @Value("${batch.arena.antallNyeIdenter}")
    private int arenaAntallNyeIdenter;

    @Value("${batch.medl.prosentfaktor}")
    private double medlProsentfaktor;


    private final TestnorgeInntektService testnorgeInntektService;
    private final TestnorgeSigrunService testnorgeSigrunService;
    private final TestnorgeAaregService testnorgeAaregService;
    private final TestnorgeInstService testnorgeInstService;
    private final TestnorgeTpService testnorgeTpService;
    private final TestnorgeSamService testnorgeSamService;
    private final ArenaService arenaService;
    private final TestnorgeMedlService testnorgeMedlService;


    @Scheduled(cron = "0 0 1 1 * *")
    public void inntektSyntBatch() {
        var request = new SyntetiserInntektsmeldingRequest(avspillergruppeId, miljoe);
        var feiledeInntektsmeldinger = testnorgeInntektService.genererInntektsmeldinger(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger. Meldinger som feilet: {}.", feiledeInntektsmeldinger.keySet().toString());
    }

    @Scheduled(cron = "0 0 1 1 5 *")
    public void poppSyntBatch() {
        var syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, poppbatchAntallNyeIdenter);
        var testdataEier = "synt_test";
        testnorgeSigrunService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void aaregSyntBatch() {
        var syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, aaregbatchAntallNyeIdenter);
        testnorgeAaregService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, true);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void instSyntBatch() {
        var syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, instbatchAntallNyeIdenter);
        testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);
    }

    @Scheduled(cron = "0 0 0 1 5 *")
    public void tpSyntBatch() {
        var request = new SyntetiserTpRequest(avspillergruppeId, miljoe, tpAntallPersoner);
        var entity = testnorgeTpService.genererTp(request);
        if (!entity.getStatusCode().is2xxSuccessful()) {
            log.error("Klarte ikke å fullføre syntetisering i TP batch kjøring");
        }
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void samSyntBatch() {
        var syntetiserSamRequest = new SyntetiserSamRequest(avspillergruppeId, miljoe, samAntallMeldinger);
        testnorgeSamService.genererSamordningsmeldinger(syntetiserSamRequest);
    }

    /**
     * Denne metoden oppretter vedtakshistorikk i Arena og registerer brukere med oppfølging (uten vedtak) i Arena.
     * Metoden kjøres hver time.
     */
    @Scheduled(cron = "0 0 0-23 * * *")
    public void arenaSyntBatch() {
        arenaService.opprettArenaVedtakshistorikk(SyntetiserArenaRequest.builder()
                .miljoe(miljoe)
                .antallNyeIdenter(arenaAntallNyeIdenter)
                .build());
        arenaService.opprettArbeidssoekereMedOppfoelgingIArena(SyntetiserArenaRequest.builder()
                .miljoe(miljoe)
                .antallNyeIdenter(1)
                .build());

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void medlSyntBatch() {
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, medlProsentfaktor);
        testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);
    }
}
