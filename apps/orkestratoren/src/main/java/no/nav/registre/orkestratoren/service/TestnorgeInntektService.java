package no.nav.registre.orkestratoren.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInntektConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Service
@Slf4j
public class TestnorgeInntektService {

    @Autowired
    private TestnorgeInntektConsumer testnorgeInntektConsumer;

    public Map<String, List<Object>> genererInntektsmeldinger(SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        return testnorgeInntektConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);
    }
}
