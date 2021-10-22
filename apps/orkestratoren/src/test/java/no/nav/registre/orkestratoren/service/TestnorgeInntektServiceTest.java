package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInntektConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@ExtendWith(MockitoExtension.class)

public class TestnorgeInntektServiceTest {

    @Mock
    private TestnorgeInntektConsumer testnorgeInntektConsumer;

    @InjectMocks
    private TestnorgeInntektService testnorgeInntektService;

    private final String fnr1 = "01010101010";
    private final Map<String, List<Object>> expectedFeiledeInntektsmeldinger = new HashMap<>();
    private final String inntektsmelding = "Feilet inntektsmelding";

    @BeforeEach
    public void setUp() {
        expectedFeiledeInntektsmeldinger.put(fnr1, new ArrayList<>(Collections.singletonList(inntektsmelding)));
    }

    @Test
    public void shouldGenerereInntektsmeldinger() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(avspillergruppeId, miljoe);

        when(testnorgeInntektConsumer.startSyntetisering(syntetiserInntektsmeldingRequest)).thenReturn(expectedFeiledeInntektsmeldinger);

        var response = testnorgeInntektService.genererInntektsmeldinger(syntetiserInntektsmeldingRequest);

        assertThat(response.get(fnr1), IsIterableContainingInOrder.contains(inntektsmelding));
        verify(testnorgeInntektConsumer).startSyntetisering(syntetiserInntektsmeldingRequest);
    }
}