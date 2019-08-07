package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.domain.Arbeidsoeker;
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

import static org.hamcrest.CoreMatchers.containsString;
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

    private String miljoe = "q2";
    private Long avspillegruppeId = 10L;
    private int antallNyeIdenter = 3;

    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";
    private String fnr4 = "40404040404";

    private Arbeidsoeker arb1 = Arbeidsoeker.builder().personident(fnr1).build();
    private Arbeidsoeker arb2 = Arbeidsoeker.builder().personident(fnr2).build();
    private Arbeidsoeker arb3 = Arbeidsoeker.builder().personident(fnr3).build();

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillegruppeId, miljoe, antallNyeIdenter);
    }


    @Test
    public void registrerAntallIdenterIArenaForvalter() {
        when(syntetiseringService
                .byggArbeidsoekereOgLagreIHodejegeren(antallNyeIdenter, avspillegruppeId, miljoe))
                .thenReturn(Arrays.asList(arb1,arb2,arb3));

        ResponseEntity<List<String>> result = syntetiseringController.registerBrukereIArenaForvalter(syntetiserArenaRequest);
        assertThat(result.getBody().get(1), containsString(fnr2));
        assertThat(result.getBody().size(), is(3));
    }

    @Test
    public void slettIdenterIArenaForvalter() {
        when(syntetiseringService
                .slettBrukereIArenaForvalter(Arrays.asList(fnr1, fnr2, fnr3, fnr4), miljoe))
                .thenReturn(Arrays.asList(fnr1, fnr3, fnr4));

        ResponseEntity<List<String>> response = syntetiseringController.slettBrukereIArenaForvalter(miljoe, Arrays.asList(fnr1, fnr2, fnr3, fnr4));
        assertThat(response.getBody(), is(Arrays.asList(fnr1, fnr3, fnr4)));
    }


}
