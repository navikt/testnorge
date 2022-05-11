package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInntektConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Service
@RequiredArgsConstructor
public class TestnorgeInntektService {

    private final TestnorgeInntektConsumer testnorgeInntektConsumer;

    public Map<String, List<Object>> genererInntektsmeldinger(SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        return testnorgeInntektConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);
    }
}
