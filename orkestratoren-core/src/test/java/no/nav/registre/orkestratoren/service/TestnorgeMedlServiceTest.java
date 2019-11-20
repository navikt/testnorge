package no.nav.registre.orkestratoren.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeMedlConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeMedlServiceTest {

    @Mock
    private TestnorgeMedlConsumer testnorgeMedlConsumer;

    @InjectMocks
    private TestnorgeMedlService testnorgeMedlService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";

    @Test
    public void shouldGenerereMedlemskap() {
        var prosentfaktor = 0.1;
        var expectedResponse = "someResponse";
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, prosentfaktor);

        when(testnorgeMedlConsumer.startSyntetisering(syntetiserMedlRequest)).thenReturn(expectedResponse);

        testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);

        verify(testnorgeMedlConsumer).startSyntetisering(syntetiserMedlRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnUgyldigProsentfaktor() {
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 1.2);
        testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);
    }
}