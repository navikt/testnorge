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

    @Test
    public void shouldGenerereInstitusjonsforhold() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;
        var expectedResponse = "someResponse";
        var syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeInstConsumer.startSyntetisering(syntetiserInstRequest)).thenReturn(expectedResponse);

        testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);

        verify(testnorgeInstConsumer).startSyntetisering(syntetiserInstRequest);
    }
}