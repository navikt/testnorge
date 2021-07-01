package no.nav.registre.testnorge.arena.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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

import no.nav.registre.testnorge.arena.consumer.rs.BrukereArenaForvalterConsumer;
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
    private IdentService identService;

    @InjectMocks
    private BrukereService brukereService;

    private final Long avspillergruppeId = 10L;
    private final String miljoe = "q2";
    private final String fnr1 = "10101010101";
    private final String fnr2 = "20202020202";
    private final String fnr3 = "30303030303";

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
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER)).thenReturn(toIdenterOverAlder);
        when(identService.hentEksisterendeArbeidsoekerIdenter(anyBoolean())).thenReturn(toIdenterOverAlder);
        when(brukereArenaForvalterConsumer.sendTilArenaForvalter(anyList())).thenReturn(enNyArbeidsoekerResponse);

        return brukereService.opprettArbeidsoekere(antallNyeIdenter, avspillergruppeId, miljoe);
    }

    @Test
    public void fyllOverfullArenaForvalter() {
        NyeBrukereResponse nyeIdenter = opprettIdenter(null, miljoe);

        assertThat(nyeIdenter.getArbeidsoekerList()).isEmpty();
        assertThat(nyeIdenter.getNyBrukerFeilList()).isEmpty();
    }

    @Test
    public void fyllFraTomArenaForvalter() {
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER)).thenReturn(hundreIdenterOverAlder);
        when(identService.hentEksisterendeArbeidsoekerIdenter(anyBoolean())).thenReturn(Collections.emptyList());
        when(brukereArenaForvalterConsumer.sendTilArenaForvalter(anyList())).thenReturn(tyveNyeArbeidsoekereResponse);

        NyeBrukereResponse arbeidsokere =
                brukereService.opprettArbeidsoekere(null, avspillergruppeId, miljoe);
        assertThat(arbeidsokere.getArbeidsoekerList()).hasSize(20);
        assertThat(arbeidsokere.getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr1);
        assertThat(arbeidsokere.getArbeidsoekerList().get(4).getPersonident()).isEqualTo("50505050505");

    }

    @Test
    public void hentGyldigeIdenterTest() {
        NyeBrukereResponse nyeIdenter = opprettIdenter(1, miljoe);

        assertThat(nyeIdenter.getArbeidsoekerList()).hasSize(1);
        assertThat(nyeIdenter.getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr2);
    }

    @Test
    public void opprettForMangeNyeIdenter() {
        NyeBrukereResponse nyeIdenter = opprettIdenter(2, miljoe);

        assertThat(nyeIdenter.getArbeidsoekerList()).hasSize(1);
        assertThat(nyeIdenter.getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr2);
    }

    @Test
    public void fyllOppForvalterenTest() {
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER)).thenReturn(hundreIdenterOverAlder);
        when(identService.hentEksisterendeArbeidsoekerIdenter(anyBoolean())).thenReturn(femtenFnr);
        when(brukereArenaForvalterConsumer.sendTilArenaForvalter(anyList())).thenReturn(opprettedeArbeidsoekereResponse);

        NyeBrukereResponse arbeidsokere =
                brukereService.opprettArbeidsoekere(null, avspillergruppeId, miljoe);

        assertThat(arbeidsokere.getArbeidsoekerList()).hasSize(5);
        assertThat(arbeidsokere.getArbeidsoekerList().get(2).getPersonident()).isEqualTo(fnr3);
        assertThat(arbeidsokere.getArbeidsoekerList().get(3).getPersonident()).isEqualTo("40404040404");
    }

    @Test
    public void opprettArbeidssoekerTest() {
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER)).thenReturn(toIdenterOverAlder);
        when(identService.hentEksisterendeArbeidsoekerIdenter(anyBoolean())).thenReturn(Collections.emptyList());
        when(brukereArenaForvalterConsumer.sendTilArenaForvalter(anyList())).thenReturn(enNyArbeidsoekerResponse);

        NyeBrukereResponse arbeidsoeker = brukereService.opprettArbeidssoeker(fnr2, avspillergruppeId, miljoe, true);

        assertThat(arbeidsoeker.getArbeidsoekerList()).hasSize(1);
        assertThat(arbeidsoeker.getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr2);
    }

    @Test
    public void opprettEksisterendeArbeidssoekerTest() {
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER)).thenReturn(toIdenterOverAlder);
        when(identService.hentEksisterendeArbeidsoekerIdenter(anyBoolean())).thenReturn(Collections.singletonList(fnr2));
        when(brukereArenaForvalterConsumer.hentArbeidsoekere(anyString(), eq(null), eq(null), anyBoolean())).thenReturn(enNyArbeisoker);

        NyeBrukereResponse arbeidsoeker = brukereService.opprettArbeidssoeker(fnr2, avspillergruppeId, miljoe, true);

        assertThat(arbeidsoeker.getArbeidsoekerList()).hasSize(1);
        assertThat(arbeidsoeker.getArbeidsoekerList().get(0).getPersonident()).isEqualTo(fnr2);
    }

    @Test
    public void opprettIkkeEksisterendeIdentTest() {
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER)).thenReturn(toIdenterOverAlder);
        when(identService.hentEksisterendeArbeidsoekerIdenter(anyBoolean())).thenReturn(Collections.emptyList());

        NyeBrukereResponse arbeidsoeker = brukereService.opprettArbeidssoeker(fnr3, avspillergruppeId, miljoe, true);

        assertThat(arbeidsoeker.getArbeidsoekerList()).isEmpty();
    }
}
