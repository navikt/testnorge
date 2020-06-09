package no.nav.registre.endringsmeldinger.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.transform.TransformerException;

import no.nav.registre.endringsmeldinger.provider.rs.requests.SyntetiserNavEndringsmeldingerRequest;
import no.nav.registre.endringsmeldinger.service.EndringsmeldingService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private EndringsmeldingService endringsmeldingService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest;

    @Before
    public void setUp() {
        syntetiserNavEndringsmeldingerRequest = SyntetiserNavEndringsmeldingerRequest.builder().build();
    }

    @Test
    public void shouldStartSyntetisering() throws TransformerException {
        syntetiseringController.genererNavMeldinger(syntetiserNavEndringsmeldingerRequest);
        verify(endringsmeldingService).opprettSyntetiskeNavEndringsmeldinger(syntetiserNavEndringsmeldingerRequest);
    }
}