package no.nav.registre.arena.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import no.nav.registre.arena.core.service.util.IdenterUtils;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
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

import no.nav.registre.arena.core.consumer.rs.BrukereArenaForvalterConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class BrukereServiceTest {

    private static final int ANTALL_EKSISTERENDE_ARBEIDSSOKERE = 15;
    private static final int ANTALL_OPPRETTEDE_ARBEIDSSOKERE = 5;
    private static final int ANTALL_LEVENDE_IDENTER = 100;
    private static final int MINIMUM_ALDER = 16;
    private static final int MAKSIMUM_ALDER = 67;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;
    @Mock
    private BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;
    @Mock
    private Random random;
    @Mock
    private IdenterUtils identerUtils;

    @InjectMocks
    private BrukereService brukereService;

    private Long avspillergruppeId = 10L;
    private String miljoe = "q2";
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";

    private List<String> toIdenterOverAlder;
    private List<String> hundreIdenterOverAlder;

    private List<Arbeidsoeker> enNyArbeisoker;

    private List<String> femtenFnr;

    private NyeBrukereResponse opprettedeArbeidsoekereResponse;
    private NyeBrukereResponse enNyArbeidsoekerResponse;
    private NyeBrukereResponse tyveNyeArbeidsoekereResponse;

    @Before
    public void setUp() {

        toIdenterOverAlder = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        hundreIdenterOverAlder = new ArrayList<>(ANTALL_LEVENDE_IDENTER);

        enNyArbeisoker = Collections.singletonList(
                buildArbeidsoker(fnr2));
        enNyArbeidsoekerResponse = new NyeBrukereResponse();
        enNyArbeidsoekerResponse.setArbeidsoekerList(enNyArbeisoker);

        femtenFnr = new ArrayList<>();
        for (int i = 1; i < ANTALL_EKSISTERENDE_ARBEIDSSOKERE + 1; i++)
            femtenFnr.add(buildFnr(i));

        List<Arbeidsoeker> opprettedeArbeidsokere = new ArrayList<>(ANTALL_OPPRETTEDE_ARBEIDSSOKERE);
        opprettedeArbeidsoekereResponse = new NyeBrukereResponse();
        for (int i = 1; i < ANTALL_OPPRETTEDE_ARBEIDSSOKERE + 1; i++)
            opprettedeArbeidsokere.add(buildArbeidsoker(buildFnr(i)));
        opprettedeArbeidsoekereResponse.setArbeidsoekerList(opprettedeArbeidsokere);

        List<Arbeidsoeker> tyveNyeArbeidsokere = new ArrayList<>(21);
        tyveNyeArbeidsoekereResponse = new NyeBrukereResponse();
        for (int i = 1; i < 21; i++)
            tyveNyeArbeidsokere.add(buildArbeidsoker(buildFnr(i)));
        tyveNyeArbeidsoekereResponse.setArbeidsoekerList(tyveNyeArbeidsokere);

        for (int i = 1; i < ANTALL_LEVENDE_IDENTER + 1; i++)
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

    private NyeBrukereResponse opprettIdenter(Integer antallNyeIdenter, String miljoe) {
        doReturn(toIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
        doReturn(toIdenterOverAlder).when(identerUtils).hentEksisterendeArbeidsoekerIdenter();
        doReturn(enNyArbeidsoekerResponse).when(brukereArenaForvalterConsumer).sendTilArenaForvalter(anyList());

        return brukereService.opprettArbeidsoekere(antallNyeIdenter, avspillergruppeId, miljoe);
    }

    @Test
    public void fyllOverfullArenaForvalter() {
        NyeBrukereResponse nyeIdenter = opprettIdenter(null, miljoe);

        assertThat(nyeIdenter.getArbeidsoekerList(), is(Collections.EMPTY_LIST));
        assertThat(nyeIdenter.getNyBrukerFeilList(), is(Collections.EMPTY_LIST));
    }

    @Test
    public void fyllFraTomArenaForvalter() {
        doReturn(hundreIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
        doReturn(Collections.EMPTY_LIST).when(identerUtils).hentEksisterendeArbeidsoekerIdenter();
        doReturn(tyveNyeArbeidsoekereResponse).when(brukereArenaForvalterConsumer).sendTilArenaForvalter(anyList());

        NyeBrukereResponse arbeidsokere =
                brukereService.opprettArbeidsoekere(null, avspillergruppeId, miljoe);
        assertThat(arbeidsokere.getArbeidsoekerList().size(), is(20));
        assertThat(arbeidsokere.getArbeidsoekerList().get(0).getPersonident(), is(fnr1));
        assertThat(arbeidsokere.getArbeidsoekerList().get(4).getPersonident(), is("50505050505"));

    }

    @Test
    public void hentGyldigeIdenterTest() {
        NyeBrukereResponse nyeIdenter = opprettIdenter(1, miljoe);

        assertThat(nyeIdenter.getArbeidsoekerList().size(), is(1));
        assertThat(nyeIdenter.getArbeidsoekerList().get(0).getPersonident(), is(fnr2));
    }

    @Test
    public void opprettForMangeNyeIdenter() {
        NyeBrukereResponse nyeIdenter = opprettIdenter(2, miljoe);

        assertThat(nyeIdenter.getArbeidsoekerList().size(), is(1));
        assertThat(nyeIdenter.getArbeidsoekerList().get(0).getPersonident(), is(fnr2));
    }

    @Test
    public void fyllOppForvalterenTest() {
        doReturn(hundreIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
        doReturn(femtenFnr).when(identerUtils).hentEksisterendeArbeidsoekerIdenter();
        doReturn(opprettedeArbeidsoekereResponse).when(brukereArenaForvalterConsumer).sendTilArenaForvalter(anyList());

        NyeBrukereResponse arbeidsokere =
                brukereService.opprettArbeidsoekere(null, avspillergruppeId, miljoe);

        assertThat(arbeidsokere.getArbeidsoekerList().size(), is(5));
        assertThat(arbeidsokere.getArbeidsoekerList().get(2).getPersonident(), is(fnr3));
        assertThat(arbeidsokere.getArbeidsoekerList().get(3).getPersonident(), is("40404040404"));
    }

    @Test
    public void opprettArbeidssoekerTest() {
        doReturn(toIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
        doReturn(Collections.emptyList()).when(identerUtils).hentEksisterendeArbeidsoekerIdenter();
        doReturn(enNyArbeidsoekerResponse).when(brukereArenaForvalterConsumer).sendTilArenaForvalter(anyList());

        NyeBrukereResponse arbeidsoeker = brukereService.opprettArbeidssoeker(fnr2, avspillergruppeId, miljoe);

        assertThat(arbeidsoeker.getArbeidsoekerList().size(), is(1));
        assertThat(arbeidsoeker.getArbeidsoekerList().get(0).getPersonident(), is(fnr2));
    }

    @Test
    public void opprettEksisterendeArbeidssoekerTest() {
        doReturn(toIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
        doReturn(Collections.singletonList(fnr2)).when(identerUtils).hentEksisterendeArbeidsoekerIdenter();
        doReturn(enNyArbeisoker).when(brukereArenaForvalterConsumer).hentArbeidsoekere(anyString(), eq(null), eq(null));

        NyeBrukereResponse arbeidsoeker = brukereService.opprettArbeidssoeker(fnr2, avspillergruppeId, miljoe);

        assertThat(arbeidsoeker.getArbeidsoekerList().size(), is(1));
        assertThat(arbeidsoeker.getArbeidsoekerList().get(0).getPersonident(), is(fnr2));
    }

    @Test
    public void opprettIkkeEksisterendeIdentTest() {
        doReturn(toIdenterOverAlder).when(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
        doReturn(Collections.emptyList()).when(identerUtils).hentEksisterendeArbeidsoekerIdenter();

        NyeBrukereResponse arbeidsoeker = brukereService.opprettArbeidssoeker(fnr3, avspillergruppeId, miljoe);

        assertThat(arbeidsoeker.getArbeidsoekerList(), is(Collections.EMPTY_LIST));
    }
}
