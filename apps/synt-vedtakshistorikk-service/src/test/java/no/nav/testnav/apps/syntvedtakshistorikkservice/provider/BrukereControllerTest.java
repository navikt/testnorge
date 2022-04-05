package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaForvalterService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.IdentService;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MINIMUM_ALDER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.VedtakshistorikkService.SYNT_TAGS;


@RunWith(MockitoJUnitRunner.class)
public class BrukereControllerTest {

    @Mock
    private ArenaForvalterService arenaForvalterService;

    @Mock
    private IdentService identService;

    @Mock
    private PdlProxyConsumer pdlProxyConsumer;

    @InjectMocks
    private BrukereController brukereController;

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
        var identer = Collections.singletonList(fnr1);
        var personer = Collections.singletonList(PersonDTO.builder().ident(fnr1).build());

        when(identService.getUtvalgteIdenterIAldersgruppe(1, MINIMUM_ALDER, MAKSIMUM_ALDER, null)).thenReturn(personer);
        when(pdlProxyConsumer.createTags(identer, SYNT_TAGS)).thenReturn(true);
        when(arenaForvalterService
                .opprettArbeidssoekereUtenVedtak(identer, miljoe))
                .thenReturn(oppfoelgingResponse);

        ResponseEntity<Map<String, NyeBrukereResponse>> result = brukereController.registrerBrukereIArenaForvalterMedOppfoelging(syntetiserArenaRequestSingle);
        assertThat(result.getBody().keySet()).hasSize(1);
        assertThat(result.getBody()).containsKey(fnr1);
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
        assertThat(result.getBody().get(fnr1).getArbeidsoekerList()).hasSize(1);
    }
}
