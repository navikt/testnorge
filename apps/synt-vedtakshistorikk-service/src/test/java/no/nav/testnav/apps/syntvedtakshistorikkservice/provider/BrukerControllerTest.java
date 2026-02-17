package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaDagpengerService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaForvalterService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.IdentService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.TagsService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonDTO;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MINIMUM_ALDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrukerControllerTest {

    @Mock
    private IdentService identService;

    @Mock
    private ArenaDagpengerService arenaDagpengerService;

    @Mock
    private TagsService tagsService;

    @Mock
    private ArenaForvalterService arenaForvalterService;

    @InjectMocks
    private BrukerController brukerController;

    private SyntetiserArenaRequest syntetiserArenaRequestSingle;

    private final String miljoe = "q2";
    private final String fnr1 = "10101010101";
    private final Arbeidsoeker arb1 = Arbeidsoeker.builder().personident(fnr1).build();

    private NyeBrukereResponse singleResponse;
    private Map<String, NyeBrukereResponse> oppfoelgingResponse;

    @BeforeEach
    void setUp() {
        syntetiserArenaRequestSingle = new SyntetiserArenaRequest(miljoe, 1);

        singleResponse = new NyeBrukereResponse();
        singleResponse.setArbeidsoekerList(Collections.singletonList(arb1));

        oppfoelgingResponse = new HashMap<>();
        oppfoelgingResponse.put(fnr1, singleResponse);
    }

    @Test
    void registrerAntallIdenterMedOppfoelgingIArenaForvalter() {
        var identer = Collections.singletonList(fnr1);
        var personer = Collections.singletonList(PersonDTO.builder().ident(fnr1).build());

        when(identService.getUtvalgteIdenterIAldersgruppe(1, MINIMUM_ALDER, MAKSIMUM_ALDER, false)).thenReturn(personer);
        when(tagsService.opprettetTagsPaaIdenterOgPartner(personer)).thenReturn(true);
        when(arenaForvalterService
                .opprettArbeidssoekereUtenVedtak(identer, miljoe))
                .thenReturn(oppfoelgingResponse);

        Map<String, NyeBrukereResponse> result = brukerController.registrerBrukereIArenaForvalterMedOppfoelging(syntetiserArenaRequestSingle).block();
        assertThat(result.keySet()).hasSize(1);
        assertThat(result).containsKey(fnr1);
        assertThat(result.get(fnr1).getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
        assertThat(result.get(fnr1).getArbeidsoekerList()).hasSize(1);
    }

    @Test
    void checkExceptionOccursOnBadMiljoe() {
        var request = new SyntetiserArenaRequest("test", 1);
        assertThrows(ResponseStatusException.class, () -> {
            brukerController.registrerBrukereIArenaForvalterMedOppfoelging(request);
        });
    }

}
