package no.nav.registre.orkestratoren.batch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.AaregSyntPakkenService;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.InstSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@Component
@EnableScheduling
@Getter
@Slf4j
public class JobController {

    @Value("#{${batch.avspillergruppeId.miljoe}}")
    private Map<Long, String> avspillergruppeIdMedMiljoe;

    @Value("#{${batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallMeldingerPerEndringskode;

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

    @Value("#{'${tpbatch.miljoe}'.split(', ')}")
    private List<String> tpbatchMiljoe;

    @Value("${tpbatch.antallIdenter}")
    private Integer tpAntallIdenter;

    @Value("${tpbatch.avspillergruppeId}")
    private String tpAvspillergruppeId;

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

    @Autowired
    private InstSyntPakkenService instSyntPakkenService;

    @Autowired
    private TpService tpService;

    @Scheduled(cron = "${tpsbatch.cron:0 0 0 * * *}")
    public void tpsSyntBatch() {
        for(Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            try {
                tpsSyntPakkenService.genererSkdmeldinger(entry.getKey(), entry.getValue(), antallMeldingerPerEndringskode);
            } catch (HttpStatusCodeException e) {
                log.warn(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Scheduled(cron = "${inntektbatch.cron:0 0 1 1 * *}")
    public void arenaInntektSyntBatch() {
        SyntetiserInntektsmeldingRequest request = new SyntetiserInntektsmeldingRequest(inntektbatchAvspillergruppeId);
        String arenaInntektId = arenaInntektSyntPakkenService.genererInntektsmeldinger(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med meldinger og mottat id {}.", arenaInntektId);
    }

    @Scheduled(cron = "${eiabatch.cron:0 0 0 * * *}")
    public void eiaSyntBatch() {
        for(Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserEiaRequest request = new SyntetiserEiaRequest(entry.getKey(), entry.getValue(), antallSykemeldinger);
            List<String> fnrMedGenererteMeldinger = eiaSyntPakkenService.genererEiaSykemeldinger(request);
            log.info("eiabatch har opprettet {} sykemeldinger i miljø {}. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), entry.getValue(), Arrays.toString(fnrMedGenererteMeldinger.toArray()));
        }
    }

    @Scheduled(cron = "${poppbatch.cron:0 0 1 1 5 *}")
    public void poppSyntBatch() {
        for(Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(entry.getKey(), entry.getValue(), poppbatchAntallNyeIdenter);
            String testdataEier = "orkestratoren";
            poppSyntPakkenService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
        }
    }

    @Scheduled(cron = "${aaregbatch.cron:0 0 1 1 * *}")
    public void aaregSyntBatch() {
        for(Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserAaregRequest syntetiserAaregRequest = new SyntetiserAaregRequest(entry.getKey(), entry.getValue(), aaregbatchAntallNyeIdenter);
            aaregSyntPakkenService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest);
        }
    }

    @Scheduled(cron = "${instbatch.cron:0 0 0 * * *}")
    public void instSyntBatch() {
        for(String miljoe : instbatchMiljoe) {
            SyntetiserInstRequest syntetiserInstRequest = new SyntetiserInstRequest(instbatchAvspillergruppeId, miljoe, instbatchAntallNyeIdenter);
            instSyntPakkenService.genererInstitusjonsforhold(syntetiserInstRequest);
        }
    }

    @Scheduled(cron = "${tpbatch.cron:0 0 0 1 5 *}")
    public void tpSyntBatch() {
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            SyntetiserTpRequest request = new SyntetiserTpRequest(entry.getKey(), entry.getValue(), tpAntallIdenter);
            HttpStatus httpStatus = tpService.genererTp(request);
            if (!httpStatus.is2xxSuccessful()) {
                log.error("Klarte ikke å fullføre syntetisering i batch kjøring");
            }
        }
    }
}
