package no.nav.registre.arena.core.service;


import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.testnorge.consumers.HodejegerenConsumer;
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

    private static final int ANTALL_EKSISTERENDE_ARBEIDSSOKERE = 15;
    private static final int ANTALL_OPPRETTEDE_ARBEIDSSOKERE = 5;
    private static final int ANTALL_LEVENDE_IDENTER = 100;
    private static final int MINIMUM_ALDER = 16;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;
    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;
    @Mock
    private Random random;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 10L;
    private String miljoe = "q2";
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";

    private List<String> toIdenterOverAlder;
    private List<String> hundreIdenterOverAlder;

    private List<Arbeidsoeker> opprettedeArbeidsokere;
    private List<Arbeidsoeker> enNyArbeisoker;
    private List<Arbeidsoeker> tyveNyeArbeidsokere;
    private List<Arbeidsoeker> toEksisterendeArbeidsokere;
    private List<Arbeidsoeker> femtenEksisterendeArbeidsokere;

    @Before
    public void setUp() {
        toIdenterOverAlder = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        hundreIdenterOverAlder = new ArrayList<>(ANTALL_LEVENDE_IDENTER);

        enNyArbeisoker = Collections.singletonList(
                buildArbeidsoker(fnr2));

        toEksisterendeArbeidsokere = Arrays.asList(
                buildArbeidsoker(fnr1),
                buildArbeidsoker(fnr3));

        femtenEksisterendeArbeidsokere = new ArrayList<>();
        for (int i = 1; i < ANTALL_EKSISTERENDE_ARBEIDSSOKERE +1; i++)
            femtenEksisterendeArbeidsokere.add(buildArbeidsoker(buildFnr(i)));

        opprettedeArbeidsokere = new ArrayList<>();
        for (int i = 1; i < ANTALL_OPPRETTEDE_ARBEIDSSOKERE +1; i++)
            opprettedeArbeidsokere.add(buildArbeidsoker(buildFnr(i)));

        tyveNyeArbeidsokere = new ArrayList<>();
        for (int i = 1; i < 21; i++)
            tyveNyeArbeidsokere.add(buildArbeidsoker(buildFnr(i)));

        for (int i = 1; i < ANTALL_LEVENDE_IDENTER +1; i++)
            hundreIdenterOverAlder.add(buildFnr(i));

    }

    private Arbeidsoeker buildArbeidsoker(String fnr) {
        return Arbeidsoeker.builder()
                .personident(fnr)
                .miljoe(miljoe)
                .status("OK")
                .eier("ORKESTRATOREN")
                .servicebehov(true)
                .automatiskInnsendingAvMeldekort(true).build();
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

    private List<Arbeidsoeker> opprettIdenter(Integer antallNyeIdenter, String miljoe) {
        doReturn(toIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER);
        doReturn(toEksisterendeArbeidsokere).when(arenaForvalterConsumer).hentArbeidsoekere();
        doReturn(enNyArbeisoker).when(arenaForvalterConsumer).sendTilArenaForvalter(anyList());

        return syntetiseringService.opprettArbeidsoekere(antallNyeIdenter, avspillergruppeId, miljoe);
    }

    @Test
    public void fyllOverfullArenaForvalter() {
        List<Arbeidsoeker> nyeIdenter = opprettIdenter(null, miljoe);

        assertThat(nyeIdenter, is(Collections.EMPTY_LIST));
    }

    @Test
    public void fyllFraTomArenaForvalter() {
        doReturn(hundreIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER);
        doReturn(Collections.EMPTY_LIST).when(arenaForvalterConsumer).hentArbeidsoekere();
        doReturn(tyveNyeArbeidsokere).when(arenaForvalterConsumer).sendTilArenaForvalter(anyList());

        List<Arbeidsoeker> arbeidsokere =
                syntetiseringService.opprettArbeidsoekere(null, avspillergruppeId, miljoe);
        assertThat(arbeidsokere.size(), is(20));
        assertThat(arbeidsokere.get(0).getPersonident(), is("10101010101"));
        assertThat(arbeidsokere.get(4).getPersonident(), is("50505050505"));

    }

    @Test
    public void hentGyldigeIdenterTest() {
        List<Arbeidsoeker> nyeIdenter = opprettIdenter(1, miljoe);

        assertThat(nyeIdenter.size(), is(1));
        assertThat(nyeIdenter.get(0).getPersonident(), is(fnr2));
    }

    @Test
    public void opprettForMangeNyeIdenter() {
        List<Arbeidsoeker> nyeIdenter = opprettIdenter(2, miljoe);

        assertThat(nyeIdenter.size(), is(1));
        assertThat(nyeIdenter.get(0).getPersonident(), is(fnr2));
    }

    @Test
    public void fyllOppForvalterenTest() {
        doReturn(hundreIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER);
        doReturn(femtenEksisterendeArbeidsokere).when(arenaForvalterConsumer).hentArbeidsoekere();
        doReturn(opprettedeArbeidsokere).when(arenaForvalterConsumer).sendTilArenaForvalter(anyList());

        List<Arbeidsoeker> arbeidsokere =
                syntetiseringService.opprettArbeidsoekere(null, avspillergruppeId, miljoe);

        assertThat(arbeidsokere.size(), is(5));
        assertThat(arbeidsokere.get(2).getPersonident(), is("30303030303"));
        assertThat(arbeidsokere.get(3).getPersonident(), is("40404040404"));
    }

    // TODO: Flytte denne testen til IdentServiceTest
//    @Test
//    public void slettBrukereTest() {
//        doReturn(true).when(arenaForvalterConsumer).slettBrukerSuccessful(eq(fnr2), eq(miljoe));
//        doReturn(true).when(arenaForvalterConsumer).slettBrukerSuccessful(eq(fnr3), eq(miljoe));
//
//        List<String> slettedeIdenter = syntetiseringService.slettBrukereIArenaForvalter(Arrays.asList(fnr1, fnr2, fnr3), miljoe);
//
//        assertThat(slettedeIdenter.contains(fnr2), is(true));
//        assertThat(slettedeIdenter.contains(fnr1), is(false));
//        assertThat(slettedeIdenter.size(), is(2));
//    }
}
