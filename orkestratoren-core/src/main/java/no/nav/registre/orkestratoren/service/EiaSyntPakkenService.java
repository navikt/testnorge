package no.nav.registre.orkestratoren.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.EiaSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;

@Service
@Slf4j
public class EiaSyntPakkenService {

    @Autowired
    private EiaSyntConsumer eiaSyntConsumer;

    public List<String> genererEiaSykemeldinger(SyntetiserEiaRequest syntetiserEiaRequest) {
        return eiaSyntConsumer.startSyntetisering(syntetiserEiaRequest);
    }
}
