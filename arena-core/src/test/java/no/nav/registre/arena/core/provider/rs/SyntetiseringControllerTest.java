package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserArenaRequest syntetiserArenaRequest;
    private SyntetiserArenaRequest syntetiserArenaRequestFyllOpp;
    private String miljoe = "q2";
    private Long avspillegruppeId = 10L;
    private int antallNyeIdenter = 3;

    private StatusFraArenaForvalterResponse status3NyeIdenter;
    private StatusFraArenaForvalterResponse status4NyeIdenter;

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillegruppeId, miljoe, antallNyeIdenter);
        syntetiserArenaRequestFyllOpp = new SyntetiserArenaRequest(avspillegruppeId, miljoe, null);
        status3NyeIdenter = new StatusFraArenaForvalterResponse();
    }


    @Test
    public void registrerAntallIdenterIArenaForvalter() {
        when(syntetiseringService.sendBrukereTilArenaForvalterConsumer(syntetiserArenaRequest)).thenReturn(status3NyeIdenter);

        ResponseEntity<String> result = syntetiseringController.registerBrukereIArenaForvalter(syntetiserArenaRequest);
    }

    @Test
    public void fyllOppIdenterIArenaForvalter() {
        when(syntetiseringService.getAntallBrukereForAaFylleArenaForvalteren(syntetiserArenaRequestFyllOpp)).thenReturn(4);
        when(syntetiseringService.sendBrukereTilArenaForvalterConsumer(syntetiserArenaRequestFyllOpp)).thenReturn(status4NyeIdenter);

        ResponseEntity<String> result = syntetiseringController.registerBrukereIArenaForvalter(syntetiserArenaRequestFyllOpp);
    }

    @Test
    public void forMangeIdenterIArenaForvalter() {
        when(syntetiseringService.getAntallBrukereForAaFylleArenaForvalteren(syntetiserArenaRequestFyllOpp)).thenReturn(-1);
        when(syntetiseringService.getProsentandelSomSkalHaMeldekort()).thenReturn(0.2);

        ResponseEntity<String> result = syntetiseringController.registerBrukereIArenaForvalter(syntetiserArenaRequestFyllOpp);
        assertThat(result.getBody(), containsString("Minst 20% identer hadde allerede meldekort."));
    }

    @Test
    public void slettIdenterIArenaForvalter() {

    }


}
