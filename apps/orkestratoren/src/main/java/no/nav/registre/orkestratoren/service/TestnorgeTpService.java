package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeTpConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@Service
@RequiredArgsConstructor
public class TestnorgeTpService {

    private final TestnorgeTpConsumer testnorgeTpConsumer;

    public ResponseEntity<String> genererTp(SyntetiserTpRequest request) {
        return testnorgeTpConsumer.startSyntetisering(request);
    }

}
