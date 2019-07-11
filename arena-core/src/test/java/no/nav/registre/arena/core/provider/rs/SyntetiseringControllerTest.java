package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
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
import java.util.Map;

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
    private SlettArenaRequest slettArenaRequest;

    private String miljoe = "q2";
    private Long avspillegruppeId = 10L;
    private int antallNyeIdenter = 3;

    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";
    private String fnr4 = "40404040404";

    private Arbeidsoker arb1 = new Arbeidsoker();
    private Arbeidsoker arb2 = new Arbeidsoker();
    private Arbeidsoker arb3 = new Arbeidsoker();
    private Arbeidsoker arb4 = new Arbeidsoker();

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillegruppeId, miljoe, antallNyeIdenter);
        syntetiserArenaRequestFyllOpp = new SyntetiserArenaRequest(avspillegruppeId, miljoe, null);
        slettArenaRequest = new SlettArenaRequest(miljoe, Arrays.asList(fnr1, fnr2, fnr3, fnr4));
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
        when(syntetiseringService.slettBrukereIArenaForvalter(slettArenaRequest)).thenReturn(Arrays.asList(fnr1, fnr3, fnr4));

        ResponseEntity<Map<String, Integer>> response = syntetiseringController.slettBrukereIArenaForvalter(slettArenaRequest);
        /*assertThat(response.getBody().get("Antall slettede identer"), is(3));
        assertThat(response.getBody().get("Identer som ikke kunne slettes"), is(1));*/
    }


}
