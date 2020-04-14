package no.nav.registre.testnorge.elsam.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.testnorge.elsam.exception.InvalidEnvironmentException;
import no.nav.registre.testnorge.elsam.provider.rs.requests.SyntetiserElsamRequest;
import no.nav.registre.testnorge.elsam.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallIdenter = 2;
    private String koeNavn = "testKoe";

    @Test
    public void shouldStartSyntetisering() throws InvalidEnvironmentException {
        SyntetiserElsamRequest syntetiserElsamRequest = new SyntetiserElsamRequest(avspillergruppeId, miljoe, antallIdenter);
        syntetiseringController.genererSykemeldinger(syntetiserElsamRequest);

        verify(syntetiseringService).syntetiserSykemeldinger(avspillergruppeId, miljoe, antallIdenter);
    }
}