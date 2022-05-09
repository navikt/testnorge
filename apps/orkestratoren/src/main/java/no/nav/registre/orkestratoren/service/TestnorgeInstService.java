package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInstConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Service
@RequiredArgsConstructor
public class TestnorgeInstService {

    private final TestnorgeInstConsumer testnorgeInstConsumer;

    public Object genererInstitusjonsforhold(SyntetiserInstRequest syntetiserInstRequest) {
        return testnorgeInstConsumer.startSyntetisering(syntetiserInstRequest);
    }
}
