package no.nav.registre.arena.core.service;


import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.domain.NyBruker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

    private static final int ANTALL_EKSISTERENDE_ARBEIDSSOKERE = 15;
    private static final int ANTALL_OPPRETTEDE_ARBEIDSSOKERE = 5;
    private static final int ANTALL_LEVENDE_IDENTER = 100;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;
    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;
    @Mock
    private Random random;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private SyntetiserArenaRequest arenaRequest;
    private SyntetiserArenaRequest arenaRequestTooMany;
    private SyntetiserArenaRequest arenaRequestFyllOpp;
    private SlettArenaRequest slettRequest;

    private Long avspillergruppeId = 10L;
    private String miljoe = "q2";
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";

    private List<String> identerOverAlder;
    private List<String> eksisterendeIdenter;
    private List<String> identerOverAlder2;

    private List<Arbeidsoker> nyeArbeisokere;
    private List<Arbeidsoker> eksisterendeArbeidsokere;
    private List<Arbeidsoker> eksisterendeArbeidsokere2;
    private List<Arbeidsoker> opprettedeArbeidsokere;

    @Before
    public void setUp() {
        identerOverAlder = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        eksisterendeIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr3));
        identerOverAlder2 = new ArrayList<>(ANTALL_LEVENDE_IDENTER);

        arenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 1);
        arenaRequestTooMany = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 2);
        arenaRequestFyllOpp = new SyntetiserArenaRequest(avspillergruppeId, miljoe, null);

        slettRequest = new SlettArenaRequest("q2", Arrays.asList(fnr1, fnr2, fnr3));

        nyeArbeisokere = Collections.singletonList(
                Arbeidsoker.builder()
                        .personident(fnr2)
                        .miljoe(miljoe)
                        .status("OK")
                        .eier("ORKESTRATOREN")
                        .servicebehov(true)
                        .automatiskInnsendingAvMeldekort(true)
                        .build());

        eksisterendeArbeidsokere = Arrays.asList(
                Arbeidsoker.builder()
                        .personident(fnr1)
                        .miljoe(miljoe)
                        .status("OK")
                        .eier("ORKESTRATOREN")
                        .servicebehov(true)
                        .automatiskInnsendingAvMeldekort(true)
                        .build(),
                Arbeidsoker.builder()
                        .personident(fnr3)
                        .miljoe(miljoe)
                        .status("OK")
                        .eier("ORKESTRATOREN")
                        .servicebehov(true)
                        .automatiskInnsendingAvMeldekort(true)
                        .build()
                );

        eksisterendeArbeidsokere2 = new ArrayList<>();
        for (int i = 1; i < ANTALL_EKSISTERENDE_ARBEIDSSOKERE +1; i++) {
            String fnr = buildFnr(i);
            eksisterendeArbeidsokere2.add(
                    Arbeidsoker.builder()
                        .personident(fnr)
                        .miljoe(miljoe)
                        .status("OK")
                        .eier("ORKESTRATOREN")
                        .servicebehov(true)
                        .automatiskInnsendingAvMeldekort(true)
                        .build());
        }
        opprettedeArbeidsokere = new ArrayList<>();
        for (int i = 1; i < ANTALL_OPPRETTEDE_ARBEIDSSOKERE +1; i++) {
            String fnr = buildFnr(i);
            opprettedeArbeidsokere.add(
                    Arbeidsoker.builder()
                        .personident(fnr)
                        .miljoe(miljoe)
                        .status("OK")
                        .eier("ORKESTRATOREN")
                        .servicebehov(true)
                        .automatiskInnsendingAvMeldekort(true)
                        .build());
        }
        for (int i = 1; i < ANTALL_LEVENDE_IDENTER +1; i++) {
            String fnr = buildFnr(i);
            identerOverAlder2.add(fnr);
        }

    }

    private String buildFnr(int id) {
        StringBuilder fnr = new StringBuilder();

        for (int j = 0; j < 5; j++) {
            fnr.append(id);
            fnr.append("0");
        }
        fnr.append(id);

        return fnr.toString();
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
        doReturn(eksisterendeArbeidsokere).when(arenaForvalterConsumer).hentBrukere();
        doReturn(nyeArbeisokere).when(arenaForvalterConsumer).sendTilArenaForvalter(anyList());

        return syntetiseringService.sendBrukereTilArenaForvalterConsumer(request);
    }

    @Test
    public void fyllOppForvalterenTest() {
        doReturn(identerOverAlder2).when(hodejegerenConsumer).finnLevendeIdenterOverAlder(avspillergruppeId);
        doReturn(eksisterendeArbeidsokere2).when(arenaForvalterConsumer).hentBrukere();
        doReturn(opprettedeArbeidsokere).when(arenaForvalterConsumer).sendTilArenaForvalter(anyList());
        assertThat(syntetiseringService.sendBrukereTilArenaForvalterConsumer(arenaRequestFyllOpp).size(), is(5));
    }

    @Test
    public void slettBrukereTest() {
        doReturn(true).when(arenaForvalterConsumer).slettBrukerSuccessful(eq(fnr2), eq(miljoe));
        doReturn(true).when(arenaForvalterConsumer).slettBrukerSuccessful(eq(fnr3), eq(miljoe));

        List<String> slettedeIdenter = syntetiseringService.slettBrukereIArenaForvalter(slettRequest);
        assertThat(slettedeIdenter.contains(fnr2), is(true));
        assertThat(slettedeIdenter.contains(fnr1), is(false));
        assertThat(slettedeIdenter.size(), is(2));
    }
}
