package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInntektConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeInntektServiceTest {

    @Mock
    private TestnorgeInntektConsumer testnorgeInntektConsumer;

    @InjectMocks
    private TestnorgeInntektService testnorgeInntektService;

    private Long avspillergruppeId = 123L;
    private String fnr1 = "01010101010";
    private Map<String, List<Object>> expectedFeiledeInntektsmeldinger = new HashMap<>();
    private String inntektsmelding = "Feilet inntektsmelding";

    @Before
    public void setUp() {
        expectedFeiledeInntektsmeldinger.put(fnr1, new ArrayList<>(Collections.singletonList(inntektsmelding)));
    }

    @Test
    public void shouldGenerereInntektsmeldinger() {
        SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(avspillergruppeId);

        when(testnorgeInntektConsumer.startSyntetisering(syntetiserInntektsmeldingRequest)).thenReturn(expectedFeiledeInntektsmeldinger);

        Map<String, List<Object>> response = testnorgeInntektService.genererInntektsmeldinger(syntetiserInntektsmeldingRequest);

        assertThat(response.get(fnr1), IsIterableContainingInOrder.contains(inntektsmelding));
        verify(testnorgeInntektConsumer).startSyntetisering(syntetiserInntektsmeldingRequest);
    }
}