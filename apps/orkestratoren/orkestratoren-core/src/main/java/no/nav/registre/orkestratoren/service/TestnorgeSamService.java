package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSamConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;

@Service
public class TestnorgeSamService {

    @Autowired
    private TestnorgeSamConsumer testnorgeSamConsumer;

    public ResponseEntity genererSamordningsmeldinger(SyntetiserSamRequest syntetiserSamRequest) {
        return testnorgeSamConsumer.startSyntetisering(syntetiserSamRequest);
    }
}
