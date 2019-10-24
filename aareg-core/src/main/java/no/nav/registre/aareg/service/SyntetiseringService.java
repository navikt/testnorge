package no.nav.registre.aareg.service;

import static no.nav.registre.aareg.consumer.ws.AaregWsConsumer.STATUS_OK;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import no.nav.registre.aareg.AaregSaveInHodejegerenRequest;
import no.nav.registre.aareg.IdentMedData;
import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@Slf4j
public class SyntetiseringService {

    private static final String AAREG_NAME = "aareg";
    private static final int MINIMUM_ALDER = 13;

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    @Autowired
    private AaregService aaregService;

    @Autowired
    private Random rand;

    public ResponseEntity opprettArbeidshistorikkOgSendTilAaregstub(SyntetiserAaregRequest syntetiserAaregRequest) {
        //        Set<String> levendeIdenter = new HashSet<>(hentLevendeIdenter(syntetiserAaregRequest.getAvspillergruppeId(), MINIMUM_ALDER));
        // for testing:
        Set<String> levendeIdenter = new HashSet<>(Collections.singletonList("19100380016"));
        Set<String> nyeIdenter = new HashSet<>(syntetiserAaregRequest.getAntallNyeIdenter());
        //        Set<String> identerIAaregstub = new HashSet<>(aaregstubConsumer.hentEksisterendeIdenter());
        // for testing:
        Set<String> identerIAaregstub = new HashSet<>();
        levendeIdenter.removeAll(identerIAaregstub);
        List<String> utvalgteIdenter = new ArrayList<>(levendeIdenter);

        int antallNyeIdenter = syntetiserAaregRequest.getAntallNyeIdenter();
        if (antallNyeIdenter > utvalgteIdenter.size()) {
            antallNyeIdenter = utvalgteIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager arbeidsforhold på {} identer.", antallNyeIdenter);
        }

        for (int i = 0; i < antallNyeIdenter; i++) {
            nyeIdenter.add(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())));
        }

        identerIAaregstub.addAll(nyeIdenter);
        List<String> lagredeIdenter = new ArrayList<>();
        List<RsAaregOpprettRequest> syntetiserteArbeidsforhold = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(new ArrayList<>(identerIAaregstub));
        for (RsAaregOpprettRequest opprettRequest : syntetiserteArbeidsforhold) {
            opprettRequest.setEnvironments(Collections.singletonList(syntetiserAaregRequest.getMiljoe()));
            RsAaregResponse response = aaregService.opprettArbeidsforhold(opprettRequest);

            if (response != null) {
                if (STATUS_OK.equals(response.getStatusPerMiljoe().get(syntetiserAaregRequest.getMiljoe()))) {
                    lagredeIdenter.add(opprettRequest.getArbeidsforhold().getArbeidstaker().getIdent());
                    aaregstubConsumer.sendTilAaregstub(Collections.singletonList(opprettRequest));
                    lagreArbeidsforholdIHodejegeren(opprettRequest);
                }
            }
        }

        StringBuilder statusFraAareg = new StringBuilder();

        if (!CollectionUtils.isEmpty(lagredeIdenter)) {
            statusFraAareg
                    .append("Identer som ble lagret i aareg: ")
                    .append(lagredeIdenter)
                    .append(". ");
        }

        log.info(statusFraAareg.toString());

        return ResponseEntity.ok().body(statusFraAareg.toString());
    }

    private void lagreArbeidsforholdIHodejegeren(RsAaregOpprettRequest opprettRequest) {
        IdentMedData identMedData = new IdentMedData(opprettRequest.getArbeidsforhold().getArbeidstaker().getIdent(), Collections.singletonList(opprettRequest.getArbeidsforhold()));
        AaregSaveInHodejegerenRequest hodejegerenRequest = new AaregSaveInHodejegerenRequest(AAREG_NAME, Collections.singletonList(identMedData));
        hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> hentLevendeIdenter(Long avspillergruppeId, int minimumAlder) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder);
    }
}
