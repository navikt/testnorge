package no.nav.registre.aareg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenConsumer;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;

@Service
public class SyntetiseringService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    @Autowired
    private Random rand;

    public ResponseEntity opprettArbeidshistorikk(SyntetiserAaregRequest syntetiserAaregRequest) {
        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenter(syntetiserAaregRequest.getAvspillergruppeId());
        List<String> utvalgteNyeIdenter = new ArrayList<>(syntetiserAaregRequest.getAntallNyeIdenter());
        List<String> eksisterendeIdenter = new ArrayList<>(aaregstubConsumer.hentEksisterendeIdenter());

        for (int i = 0; i < syntetiserAaregRequest.getAntallNyeIdenter(); i++) {
            utvalgteNyeIdenter.add(levendeIdenter.remove(rand.nextInt(levendeIdenter.size())));
        }

        eksisterendeIdenter.addAll(utvalgteNyeIdenter);
        Map<String, List<Map<String, String>>> syntetiserteArbeidsforholdsmeldinger = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(eksisterendeIdenter);

        return aaregstubConsumer.sendTilAaregstub(syntetiserteArbeidsforholdsmeldinger);
    }
}
