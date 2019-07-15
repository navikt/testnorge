package no.nav.registre.arena.core.service;


import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class SyntetisertingServiceTest {
    @Mock
    private HodejegerenConsumer hodejegerenConsumer;
    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;
    @Mock
    private Random random;

    @Mock
    List<String> eksisterendeIdenterMock;
    @Mock
    List<String> identerOverAlderMock;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private SyntetiserArenaRequest arenaRequest;
    private SyntetiserArenaRequest arenaRequestTooMany;
    private SlettArenaRequest slettRequest;
    private Long avspillergruppeId = 10L;
    private String miljoe = "q2";
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";

    private List<String> identerOverAlder = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    private List<String> eksisterendeIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr3));
    private List<Arbeidsoker> nyeArbeisokere;

    @Before
    public void setUp() {
        arenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 1);
        arenaRequestTooMany = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 2);

        slettRequest = new SlettArenaRequest("q2", Arrays.asList(fnr1, fnr2, fnr3));

        nyeArbeisokere = Collections.singletonList(new Arbeidsoker(
            fnr2,
            miljoe,
            "OK",
            "ORKESTRATOREN",
            true,
                true));
    }

    @Test
    public void hentGyldigeIdenterTest() {
        List<Arbeidsoker> nyeIdenter = opprettIdenter(arenaRequest);
        assertThat(nyeIdenter.size(), is(1));
    }

    @Test
    public void opprettForMangeNyeIdenter() {
        List<Arbeidsoker> nyeIdenter = opprettIdenter(arenaRequestTooMany);
        assertThat(nyeIdenter.size(), is(1));
    }

    private List<Arbeidsoker> opprettIdenter(SyntetiserArenaRequest request) {
        doReturn(identerOverAlder).when(hodejegerenConsumer).finnLevendeIdenterOverAlder(avspillergruppeId);
        doReturn(eksisterendeIdenter).when(arenaForvalterConsumer).hentEksisterendeIdenter();
        doReturn(nyeArbeisokere).when(arenaForvalterConsumer).sendTilArenaForvalter(anyList());

        return syntetiseringService.sendBrukereTilArenaForvalterConsumer(request);
    }

    @Test
    public void fyllOppForvalterenTest() {
        doReturn(100).when(identerOverAlderMock).size();
        doReturn(15).when(eksisterendeIdenterMock).size();

        doReturn(identerOverAlderMock).when(hodejegerenConsumer).finnLevendeIdenterOverAlder(avspillergruppeId);
        doReturn(eksisterendeIdenterMock).when(arenaForvalterConsumer).hentEksisterendeIdenter();

        assertThat(syntetiseringService.getAntallBrukereForAaFylleArenaForvalteren(arenaRequest), is(5));
    }

    @Test
    public void slettBrukereTest() {
        // doReturn(true).when(arenaForvalterConsumer).slettBrukerSuccessful(anyString(), anyString());
        doReturn(true).when(arenaForvalterConsumer).slettBrukerSuccessful(eq(fnr2), eq(miljoe));
        doReturn(true).when(arenaForvalterConsumer).slettBrukerSuccessful(eq(fnr3), eq(miljoe));

        List<String> slettedeIdenter = syntetiseringService.slettBrukereIArenaForvalter(slettRequest);
        assertThat(slettedeIdenter.contains(fnr2), is(true));
        assertThat(slettedeIdenter.contains(fnr1), is(false));
        assertThat(slettedeIdenter.size(), is(2));
    }
}
