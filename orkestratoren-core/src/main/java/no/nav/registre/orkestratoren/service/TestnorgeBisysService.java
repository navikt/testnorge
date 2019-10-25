package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeBisysConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@Service
public class TestnorgeBisysService {

    @Autowired
    private TestnorgeBisysConsumer testnorgeBisysConsumer;

    public Object genererBistandsmeldinger(SyntetiserBisysRequest syntetiserBisysRequest) {
        return testnorgeBisysConsumer.startSyntetisering(syntetiserBisysRequest);
    }
}
