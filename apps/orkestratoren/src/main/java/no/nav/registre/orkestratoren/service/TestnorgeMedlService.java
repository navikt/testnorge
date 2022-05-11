package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeMedlConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;

@Service
@RequiredArgsConstructor
public class TestnorgeMedlService {

    private final TestnorgeMedlConsumer testnorgeMedlConsumer;

    public Object genererMedlemskap(SyntetiserMedlRequest syntetiserMedlRequest) {
        if (syntetiserMedlRequest.getProsentfaktor() < 0 || syntetiserMedlRequest.getProsentfaktor() > 1) {
            throw new IllegalArgumentException("Ugyldig prosentfaktor. Oppgi et tall mellom 0 og 1");
        }

        return testnorgeMedlConsumer.startSyntetisering(syntetiserMedlRequest);
    }
}
