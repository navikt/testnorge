package no.nav.registre.aareg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenConsumer;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;

@Service
public class SyntetiseringService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Autowired
    private Random rand;

    public List<String> hentArbeidshistorikk(SyntetiserAaregRequest syntetiserAaregRequest) {
        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenter(syntetiserAaregRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(syntetiserAaregRequest.getAntallMeldinger());

        for (int i = 0; i < syntetiserAaregRequest.getAntallMeldinger(); i++) {
            utvalgteIdenter.add(levendeIdenter.remove(rand.nextInt(levendeIdenter.size())));
        }

        Map<String, List<Map<String, String>>> syntetiserteMeldinger = aaregSyntetisererenConsumer.getSyntetiserteMeldinger(utvalgteIdenter);

        return new ArrayList<>();
    }
}
