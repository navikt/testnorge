package no.nav.registre.orkestratoren.batch;

import static no.nav.registre.orkestratoren.utils.ExceptionUtils.createListOfRangesFromIds;
import static no.nav.registre.orkestratoren.utils.ExceptionUtils.extractIdsFromResponseBody;

import java.util.ArrayList;
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

    @Value("${orkestratoren.batch.avspillergruppeId}")
    private Long avspillergruppeId;

    @Value("#{${orkestratoren.batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Value("${orkestratoren.eiabatch.antallSykemeldinger}")
    private int antallSykemeldinger;

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Autowired
    private EiaSyntPakkenService eiaSyntPakkenService;

    @Scheduled(cron = "${orkestratoren.tpsbatch.cron:0 0 0 * * *}")
    public void tpsSyntBatch() {
        List<Long> ids = new ArrayList<>();
        try {
            tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(avspillergruppeId, tpsbatchMiljoe, antallMeldingerPerEndringskode);
        } catch (HttpStatusCodeException e) {
            ids.addAll(extractIdsFromResponseBody(e));
            if (!ids.isEmpty()) {
                log.warn("tpsSyntBatch: Noe feilet i produserOfSendSkdmeldingerTilTpsIMiljoer for gruppe {}. Følgende id-er ble returnert: {}. {} {}",
                        avspillergruppeId, createListOfRangesFromIds(ids), e.getResponseBodyAsString(), e);
            } else {
                log.warn(e.getResponseBodyAsString(), e);
            }
        }
    }

    @Scheduled(cron = "${orkestratoren.arenabatch.cron:0 0 1 1 * *}")
    public void arenaInntektSyntBatch() {
        SyntetiserInntektsmeldingRequest request = new SyntetiserInntektsmeldingRequest(avspillergruppeId);
        List<String> levendeNordmennFnr = arenaInntektSyntPakkenService.genererEnInntektsmeldingPerFnrIInntektstub(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med {} meldinger.", levendeNordmennFnr.size());
    }

    @Scheduled(cron = "${orkestratoren.eiabatch.cron:0 0 0 * * *}")
    public void eiaSyntBatch() {
        SyntetiserEiaRequest request = new SyntetiserEiaRequest(avspillergruppeId, eiabatchMiljoe, antallSykemeldinger);
        List<String> fnrMedGenererteMeldinger = eiaSyntPakkenService.genererEiaSykemeldinger(request);
        log.info("eiabatch har opprettet {} sykemeldinger. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), Arrays.toString(fnrMedGenererteMeldinger.toArray()));
    }
}
