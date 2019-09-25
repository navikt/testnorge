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

import no.nav.registre.orkestratoren.consumer.rs.EiaSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;

@RunWith(MockitoJUnitRunner.class)
public class EiaSyntPakkenServiceTest {

    @Mock
    private EiaSyntConsumer eiaSyntConsumer;

    @InjectMocks
    private EiaSyntPakkenService eiaSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> expectedIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr2));

    @Test
    public void shouldGenerereEiaSykemeldinger() {
        SyntetiserEiaRequest syntetiserEiaRequest = new SyntetiserEiaRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(eiaSyntConsumer.startSyntetisering(syntetiserEiaRequest)).thenReturn(expectedIdenter);

        List<String> response = eiaSyntPakkenService.genererEiaSykemeldinger(syntetiserEiaRequest);

        assertThat(response, IsIterableContainingInOrder.contains(fnr1, fnr2));
        verify(eiaSyntConsumer).startSyntetisering(syntetiserEiaRequest);
    }
}