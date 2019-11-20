package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeEiaConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeEiaServiceTest {

    @Mock
    private TestnorgeEiaConsumer testnorgeEiaConsumer;

    @InjectMocks
    private TestnorgeEiaService testnorgeEiaService;

    @Test
    public void shouldGenerereEiaSykemeldinger() {
        var expectedIdenter = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;

        var syntetiserEiaRequest = new SyntetiserEiaRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeEiaConsumer.startSyntetisering(syntetiserEiaRequest)).thenReturn(expectedIdenter);

        List<String> response = testnorgeEiaService.genererEiaSykemeldinger(syntetiserEiaRequest);

        assertThat(response, IsIterableContainingInOrder.contains(expectedIdenter.get(0), expectedIdenter.get(1)));
        verify(testnorgeEiaConsumer).startSyntetisering(syntetiserEiaRequest);
    }
}