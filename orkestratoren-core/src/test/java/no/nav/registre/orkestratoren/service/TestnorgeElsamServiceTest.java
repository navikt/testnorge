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

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeElsamConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserElsamRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeElsamServiceTest {

    @Mock
    private TestnorgeElsamConsumer testnorgeElsamConsumer;

    @InjectMocks
    private TestnorgeElsamService testnorgeElsamService;

    @Test
    public void shouldGenerereElsamSykemeldinger() {
        var expectedIdenter = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;

        var syntetiserElsamRequest = new SyntetiserElsamRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeElsamConsumer.startSyntetisering(syntetiserElsamRequest)).thenReturn(expectedIdenter);

        List<String> response = testnorgeElsamService.genererElsamSykemeldinger(syntetiserElsamRequest);

        assertThat(response, IsIterableContainingInOrder.contains(expectedIdenter.get(0), expectedIdenter.get(1)));
        verify(testnorgeElsamConsumer).startSyntetisering(syntetiserElsamRequest);
    }
}