package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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


    private Arbeidsoker arb1 = new Arbeidsoker();
    private Arbeidsoker arb2 = new Arbeidsoker();
    private Arbeidsoker arb3 = new Arbeidsoker();
    private Arbeidsoker arb4 = new Arbeidsoker();

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillegruppeId, miljoe, antallNyeIdenter);
        syntetiserArenaRequestFyllOpp = new SyntetiserArenaRequest(avspillegruppeId, miljoe, null);
    }


    @Test
    public void registrerAntallIdenterIArenaForvalter() {
        when(syntetiseringService
                .sendBrukereTilArenaForvalterConsumer(syntetiserArenaRequest))
                .thenReturn(Arrays.asList(arb1,arb2,arb3));

        ResponseEntity<Integer> result = syntetiseringController.registerBrukereIArenaForvalter(syntetiserArenaRequest);
        assertThat(result.getBody(), is(3));
    }

    @Test
    public void fyllOppIdenterIArenaForvalter() {
        when(syntetiseringService.getAntallBrukereForAaFylleArenaForvalteren(syntetiserArenaRequestFyllOpp)).thenReturn(4);
        when(syntetiseringService
                .sendBrukereTilArenaForvalterConsumer(syntetiserArenaRequestFyllOpp))
                .thenReturn(Arrays.asList(arb1,arb2,arb3,arb4));

        ResponseEntity<Integer> result = syntetiseringController.registerBrukereIArenaForvalter(syntetiserArenaRequestFyllOpp);
        assertThat(result.getBody(), is(4));
    }

    @Test
    public void forMangeIdenterIArenaForvalter() {
        when(syntetiseringService.getAntallBrukereForAaFylleArenaForvalteren(syntetiserArenaRequestFyllOpp)).thenReturn(-1);

        ResponseEntity<Integer> result = syntetiseringController.registerBrukereIArenaForvalter(syntetiserArenaRequestFyllOpp);
        assertThat(result.getBody(), is(0));
    }

    @Test
    public void slettIdenterIArenaForvalter() {

    }


}
