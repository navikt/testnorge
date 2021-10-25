package no.nav.registre.orkestratoren.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeMedlConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;

@ExtendWith(MockitoExtension.class)
public class TestnorgeMedlServiceTest {

    @Mock
    private TestnorgeMedlConsumer testnorgeMedlConsumer;

    @InjectMocks
    private TestnorgeMedlService testnorgeMedlService;

    private final Long avspillergruppeId = 123L;
    private final String miljoe = "t1";

    @Test
    public void shouldGenerereMedlemskap() {
        var prosentfaktor = 0.1;
        var expectedResponse = "someResponse";
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, prosentfaktor);

        when(testnorgeMedlConsumer.startSyntetisering(syntetiserMedlRequest)).thenReturn(expectedResponse);

        testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);

        verify(testnorgeMedlConsumer).startSyntetisering(syntetiserMedlRequest);
    }

    @Test
    public void shouldThrowExceptionOnUgyldigProsentfaktor() {
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 1.2);
        Assertions.assertThrows(IllegalArgumentException.class, () -> testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest));
    }
}