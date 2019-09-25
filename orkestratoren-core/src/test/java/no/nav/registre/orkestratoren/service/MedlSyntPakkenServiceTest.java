package no.nav.registre.orkestratoren.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.MedlConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;

@RunWith(MockitoJUnitRunner.class)
public class MedlSyntPakkenServiceTest {

    @Mock
    private MedlConsumer medlConsumer;

    @InjectMocks
    private MedlSyntPakkenService medlSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private double prosentfaktor = 0.1;
    private String expectedResponse = "someResponse";

    @Test
    public void shouldGenerereMedlemskap() {
        SyntetiserMedlRequest syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, prosentfaktor);

        when(medlConsumer.startSyntetisering(syntetiserMedlRequest)).thenReturn(expectedResponse);

        medlSyntPakkenService.genererMedlemskap(syntetiserMedlRequest);

        verify(medlConsumer).startSyntetisering(syntetiserMedlRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnUgyldigProsentfaktor() {
        SyntetiserMedlRequest syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 1.2);
        medlSyntPakkenService.genererMedlemskap(syntetiserMedlRequest);
    }
}