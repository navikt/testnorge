package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.consumer.rs.responses.NyeBrukereResponse;
import no.nav.registre.arena.domain.brukere.Arbeidsoeker;
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
import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
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

    private Arbeidsoeker arb1 = Arbeidsoeker.builder().personident(fnr1).build();
    private Arbeidsoeker arb2 = Arbeidsoeker.builder().personident(fnr2).build();
    private Arbeidsoeker arb3 = Arbeidsoeker.builder().personident(fnr3).build();

    private NyeBrukereResponse response;
    private NyeBrukereResponse singleResponse;

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillegruppeId, miljoe, antallNyeIdenter);
        response = new NyeBrukereResponse();
        response.setArbeidsoekerList(Arrays.asList(arb1, arb2, arb3));

        singleResponse = new NyeBrukereResponse();
        singleResponse.setArbeidsoekerList(Collections.singletonList(arb1));
    }


    @Test
    public void registrerAntallIdenterIArenaForvalter() {
        when(syntetiseringService
                .opprettArbeidsoekere(antallNyeIdenter, avspillegruppeId, miljoe))
                .thenReturn(response);

        ResponseEntity<NyeBrukereResponse> result = syntetiseringController.registrerBrukereIArenaForvalter(null, syntetiserArenaRequest);
        assertThat(result.getBody().getArbeidsoekerList().get(1).getPersonident(), containsString(fnr2));
        assertThat(result.getBody().getArbeidsoekerList().size(), is(3));
    }

    @Test
    public void registrerIdentIArenaForvalter() {
        doReturn(singleResponse).when(syntetiseringService)
                .opprettArbeidssoeker(fnr1, avspillegruppeId, miljoe);

        ResponseEntity<NyeBrukereResponse> response = syntetiseringController.registrerBrukereIArenaForvalter(fnr1, syntetiserArenaRequest);
        assertThat(response.getBody().getArbeidsoekerList().size(), is(1));
        assertThat(response.getBody().getArbeidsoekerList().get(0).getPersonident(), containsString(fnr1));
    }
}
