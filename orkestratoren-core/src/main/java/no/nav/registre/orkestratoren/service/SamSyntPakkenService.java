package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.SamSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;

@Service
public class SamSyntPakkenService {

    @Autowired
    private SamSyntConsumer samSyntConsumer;

    public ResponseEntity genererSamordningsmeldinger(SyntetiserSamRequest syntetiserSamRequest) {
        return samSyntConsumer.startSyntetisering(syntetiserSamRequest);
    }
}
