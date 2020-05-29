package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInstConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Service
public class TestnorgeInstService {

    @Autowired
    private TestnorgeInstConsumer testnorgeInstConsumer;

    public Object genererInstitusjonsforhold(SyntetiserInstRequest syntetiserInstRequest) {
        return testnorgeInstConsumer.startSyntetisering(syntetiserInstRequest);
    }
}
