package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.AaregSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;

@Service
public class AaregSyntPakkenService {

    @Autowired
    private AaregSyntConsumer aaregSyntConsumer;

    public ResponseEntity genererArbeidsforholdsmeldinger(SyntetiserAaregRequest syntetiserAaregRequest, boolean lagreIAareg) {
        return aaregSyntConsumer.startSyntetisering(syntetiserAaregRequest, lagreIAareg);
    }
}
