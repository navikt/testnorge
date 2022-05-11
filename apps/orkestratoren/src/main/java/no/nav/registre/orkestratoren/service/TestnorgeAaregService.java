package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestnorgeAaregService {

    private final TestnorgeAaregConsumer testnorgeAaregConsumer;

    public ResponseEntity<List<Object>> genererArbeidsforholdsmeldinger(SyntetiserAaregRequest syntetiserAaregRequest, boolean sendAlleEksisterende) {
        return testnorgeAaregConsumer.startSyntetisering(syntetiserAaregRequest, sendAlleEksisterende);
    }
}
