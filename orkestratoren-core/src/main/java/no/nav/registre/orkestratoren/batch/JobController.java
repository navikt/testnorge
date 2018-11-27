package no.nav.registre.orkestratoren.batch;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@Component
@EnableScheduling
@Getter
@Slf4j
public class JobController {

    @Value("${orkestratoren.tpsbatch.miljoe}")
    private String miljoe;

    @Value("${orkestratoren.batch.skdMeldingGruppeId}")
    private Long skdMeldingGruppeId;

    @Value("#{${orkestratoren.batch.antallMeldingerPerEndringskode}}")
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Scheduled(cron = "${orkestratoren.tpsbatch.cron:0 0 0 * * *}")
    public void tpsSyntBatch() {
        tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(skdMeldingGruppeId, miljoe, antallMeldingerPerEndringskode);
    }

    @Scheduled(cron = "${orkestratoren.arenabatch.cron:0 0 1 1 * *}")
    public void arenaInntektSyntBatch() {
        SyntetiserInntektsmeldingRequest request = SyntetiserInntektsmeldingRequest.builder()
                .skdMeldingGruppeId(skdMeldingGruppeId)
                .build();
        List<String> levendeNordmennFnr = arenaInntektSyntPakkenService.genererEnInntektsmeldingPerFnrIInntektstub(request);
        log.info("Inntekt-synt.-batch har matet Inntektstub med {} meldinger.", levendeNordmennFnr.size());
    }
}
