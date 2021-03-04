package no.nav.registre.testnorge.arena.provider.rs;

import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.arena.service.BrukereService;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BrukereControllerTest {

    @Mock
    private BrukereService brukereService;

    @InjectMocks
    private BrukereController brukereController;

    private SyntetiserArenaRequest syntetiserArenaRequest;
    private SyntetiserArenaRequest syntetiserArenaRequestSingle;

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
    private Map<String, NyeBrukereResponse> oppfoelgingResponse;

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillegruppeId, miljoe, antallNyeIdenter);
        syntetiserArenaRequestSingle = new SyntetiserArenaRequest(avspillegruppeId, miljoe, 1);
        response = new NyeBrukereResponse();
        response.setArbeidsoekerList(Arrays.asList(arb1, arb2, arb3));

        singleResponse = new NyeBrukereResponse();
        singleResponse.setArbeidsoekerList(Collections.singletonList(arb1));

        oppfoelgingResponse = new HashMap<>();
        oppfoelgingResponse.put(fnr1, singleResponse);
    }


    @Test
    public void registrerAntallIdenterIArenaForvalter() {
        when(brukereService
                .opprettArbeidsoekere(antallNyeIdenter, avspillegruppeId, miljoe))
                .thenReturn(response);

        ResponseEntity<NyeBrukereResponse> result = brukereController.registrerBrukereIArenaForvalter(null, syntetiserArenaRequest);
        assertThat(result.getBody().getArbeidsoekerList()).hasSize(antallNyeIdenter);
        assertThat(result.getBody().getArbeidsoekerList().get(1).getPersonident()).isEqualTo(fnr2);
    }

    @Test
    public void registrerIdentIArenaForvalter() {
        doReturn(singleResponse).when(brukereService)
                .opprettArbeidssoeker(fnr1, avspillegruppeId, miljoe, false);

        ResponseEntity<NyeBrukereResponse> response = brukereController.registrerBrukereIArenaForvalter(fnr1, syntetiserArenaRequest);
        assertThat(response.getBody().getArbeidsoekerList()).hasSize(1);
        assertThat(response.getBody().getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
    }

    @Test
    public void registrerAntallIdenterMedOppfoelgingIArenaForvalter() {
        when(brukereService
                .opprettArbeidssoekereUtenVedtak(1, avspillegruppeId, miljoe))
                .thenReturn(oppfoelgingResponse);

        ResponseEntity<Map<String, NyeBrukereResponse>> result = brukereController.registrerBrukereIArenaForvalterMedOppfoelging(syntetiserArenaRequestSingle);
        assertThat(result.getBody().keySet()).hasSize(1);
        assertThat(result.getBody().keySet().contains(fnr1)).isTrue();
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList()).hasSize(1);
    }
}
