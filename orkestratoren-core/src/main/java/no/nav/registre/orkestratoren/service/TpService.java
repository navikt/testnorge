package no.nav.registre.orkestratoren.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeTpConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@Service
@Slf4j
public class TpService {

    @Autowired
    TestnorgeTpConsumer testnorgeTpConsumer;

    public HttpStatus genererTp(SyntetiserTpRequest request) {
        return testnorgeTpConsumer.startSyntetisering(request);
    }

}
