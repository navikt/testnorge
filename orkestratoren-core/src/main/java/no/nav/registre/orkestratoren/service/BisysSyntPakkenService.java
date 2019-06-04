package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.BisysSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@Service
public class BisysSyntPakkenService {

    @Autowired
    private BisysSyntConsumer bisysSyntConsumer;

    public Object genererBistandsmeldinger(SyntetiserBisysRequest syntetiserBisysRequest) {
        return bisysSyntConsumer.startSyntetisering(syntetiserBisysRequest);
    }
}
