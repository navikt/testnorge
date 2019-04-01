package no.nav.registre.bisys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.bisys.consumer.rs.HodejegerenConsumer;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;

@Service
public class SyntetiseringService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public void genererBidragsmeldinger(SyntetiserBisysRequest syntetiserBisysRequest) {
        List<String> identerMedFoedselsmelding = hodejegerenConsumer.finnLevendeIdenter(syntetiserBisysRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = velgUtGyldigeIdenter(syntetiserBisysRequest.getAntallNyeIdenter(), identerMedFoedselsmelding);
    }

    public List<String> velgUtGyldigeIdenter(int antallIdenter, List<String> alleLevendeIdenter) {
        List<String> utvalgteIdenter = new ArrayList<>(antallIdenter);
        for (String ident : alleLevendeIdenter) {
            // velg ut identer som har mor/far i relasjon i status-quo-kall
            utvalgteIdenter.add(ident);
        }
        return utvalgteIdenter;
    }

}
