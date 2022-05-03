package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.BrukerService;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BrukereControllerTest {

    @Mock
    private BrukerService brukerService;

    @InjectMocks
    private BrukerController brukerController;

    private SyntetiserArenaRequest syntetiserArenaRequestSingle;

    private final String miljoe = "q2";
    private final String fnr1 = "10101010101";
    private final Arbeidsoeker arb1 = Arbeidsoeker.builder().personident(fnr1).build();

    private NyeBrukereResponse singleResponse;
    private Map<String, NyeBrukereResponse> oppfoelgingResponse;

    @Before
    public void setUp() {
        syntetiserArenaRequestSingle = new SyntetiserArenaRequest(miljoe, 1);

        singleResponse = new NyeBrukereResponse();
        singleResponse.setArbeidsoekerList(Collections.singletonList(arb1));

        oppfoelgingResponse = new HashMap<>();
        oppfoelgingResponse.put(fnr1, singleResponse);
    }

    @Test
    public void registrerAntallIdenterMedOppfoelgingIArenaForvalter() {
        when(brukerService.registrerArenaBrukereMedOppfoelging(syntetiserArenaRequestSingle)).thenReturn(oppfoelgingResponse);

        ResponseEntity<Map<String, NyeBrukereResponse>> result = brukerController.registrerBrukereIArenaForvalterMedOppfoelging(syntetiserArenaRequestSingle);
        assertThat(result.getBody().keySet()).hasSize(1);
        assertThat(result.getBody()).containsKey(fnr1);
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList()).hasSize(1);
    }

    @Test
    public void checkExceptionOccursOnBadMiljoe() {
        var request = new SyntetiserArenaRequest("test", 1);
        assertThrows(ResponseStatusException.class, () -> {
            brukerController.registrerBrukereIArenaForvalterMedOppfoelging(request);
        });
    }
}
