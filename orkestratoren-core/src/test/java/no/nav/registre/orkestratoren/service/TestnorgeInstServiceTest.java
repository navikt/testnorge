package no.nav.registre.orkestratoren.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInstConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeInstServiceTest {

    @Mock
    private TestnorgeInstConsumer testnorgeInstConsumer;

    @InjectMocks
    private TestnorgeInstService testnorgeInstService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String expectedResponse = "someResponse";

    @Test
    public void shouldGenerereInstitusjonsforhold() {
        SyntetiserInstRequest syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeInstConsumer.startSyntetisering(syntetiserInstRequest)).thenReturn(expectedResponse);

        testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);

        verify(testnorgeInstConsumer).startSyntetisering(syntetiserInstRequest);
    }
}