package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserArenaRequest syntetiserArenaRequest;
    private String miljoe = "q2";
    private Long avspillegruppeId = 10L;
    private int opprettAntallIdenter = 3;


    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillegruppeId, miljoe);
    }


    @Test
    public void registrerAntallIdenterIArenaForvalter() {

    }

    @Test
    public void fyllOppIdenterIArenaForvalter() {

    }

    @Test
    public void slettIdenterIArenaForvalter() {

    }

}
