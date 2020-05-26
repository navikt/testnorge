package no.nav.registre.medl.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.medl.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.medl.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserMedlRequest syntetiserMedlRequest;

    @Before
    public void setUp() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var prosentfaktor = 0.1;
        syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, prosentfaktor);
    }

    @Test
    public void shouldStartSyntetisering() {
        syntetiseringController.genererMedlemskapsmeldinger(syntetiserMedlRequest);
        verify(syntetiseringService).opprettMeldinger(syntetiserMedlRequest);
    }
}
