package no.nav.registre.testnorge.arena.provider.rs;

import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.arena.service.ArenaBrukerService;
import no.nav.registre.testnorge.arena.service.IdentService;
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
import static org.mockito.Mockito.when;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MINIMUM_ALDER;

@RunWith(MockitoJUnitRunner.class)
public class BrukereControllerTest {

    @Mock
    private ArenaBrukerService arenaBrukerService;
    @Mock
    private IdentService identService;

    @InjectMocks
    private BrukereController brukereController;

    private SyntetiserArenaRequest syntetiserArenaRequest;
    private SyntetiserArenaRequest syntetiserArenaRequestSingle;

    private final String miljoe = "q2";
    private final Long avspillegruppeId = 10L;
    private final int antallNyeIdenter = 3;

    private final String fnr1 = "10101010101";
    private final String fnr2 = "20202020202";
    private final String fnr3 = "30303030303";

    private final Arbeidsoeker arb1 = Arbeidsoeker.builder().personident(fnr1).build();
    private final Arbeidsoeker arb2 = Arbeidsoeker.builder().personident(fnr2).build();
    private final Arbeidsoeker arb3 = Arbeidsoeker.builder().personident(fnr3).build();

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
        when(arenaBrukerService
                .opprettArbeidsoekere(antallNyeIdenter, avspillegruppeId, miljoe))
                .thenReturn(response);

        ResponseEntity<NyeBrukereResponse> result = brukereController.registrerBrukereIArenaForvalter(null, syntetiserArenaRequest);
        assertThat(result.getBody().getArbeidsoekerList()).hasSize(antallNyeIdenter);
        assertThat(result.getBody().getArbeidsoekerList().get(1).getPersonident()).isEqualTo(fnr2);
    }

    @Test
    public void registrerIdentIArenaForvalter() {
        when(arenaBrukerService.opprettArbeidssoeker(fnr1, avspillegruppeId, miljoe, false)).thenReturn(singleResponse);

        ResponseEntity<NyeBrukereResponse> response = brukereController.registrerBrukereIArenaForvalter(fnr1, syntetiserArenaRequest);
        assertThat(response.getBody().getArbeidsoekerList()).hasSize(1);
        assertThat(response.getBody().getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
    }

    @Test
    public void registrerAntallIdenterMedOppfoelgingIArenaForvalter() {
        var identer = Collections.singletonList(fnr1);

        when(identService.getUtvalgteIdenterIAldersgruppe(avspillegruppeId, 1, MINIMUM_ALDER, MAKSIMUM_ALDER, miljoe, null)).thenReturn(identer);
        when(arenaBrukerService
                .opprettArbeidssoekereUtenVedtak(identer, miljoe))
                .thenReturn(oppfoelgingResponse);

        ResponseEntity<Map<String, NyeBrukereResponse>> result = brukereController.registrerBrukereIArenaForvalterMedOppfoelging(syntetiserArenaRequestSingle);
        assertThat(result.getBody().keySet()).hasSize(1);
        assertThat(result.getBody().keySet().contains(fnr1)).isTrue();
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList()).hasSize(1);
    }
}
