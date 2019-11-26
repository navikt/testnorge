package no.nav.registre.orkestratoren.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeElsamConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserElsamRequest;

@Service
@Slf4j
public class TestnorgeElsamService {

    @Autowired
    private TestnorgeElsamConsumer testnorgeElsamConsumer;

    public List<String> genererElsamSykemeldinger(SyntetiserElsamRequest syntetiserElsamRequest) {
        return testnorgeElsamConsumer.startSyntetisering(syntetiserElsamRequest);
    }
}
