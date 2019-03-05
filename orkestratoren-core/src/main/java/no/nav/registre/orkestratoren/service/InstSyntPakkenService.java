package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.InstSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Service
public class InstSyntPakkenService {

    @Autowired
    private InstSyntConsumer instSyntConsumer;

    public ResponseEntity genererInstitusjonsforhold(SyntetiserInstRequest syntetiserInstRequest) {
        return instSyntConsumer.startSyntetisering(syntetiserInstRequest);
    }
}
