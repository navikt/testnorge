package no.nav.registre.arena.core.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetSyntConsumer;
import no.nav.registre.arena.domain.historikk.Vedtakshistorikk;
import no.nav.registre.arena.domain.rettighet.NyRettighet;
import no.nav.registre.arena.domain.rettighet.NyRettighetResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@RunWith(MockitoJUnitRunner.class)
public class RettighetServiceTest {

    private static final int MIN_ALDER = 18;
    private static final int MAX_ALDER = 36;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private RettighetSyntConsumer rettighetSyntConsumer;

    @Mock
    private SyntetiseringService syntetiseringService;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @InjectMocks
    private RettighetService rettighetService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallIdenter = 1;
    private List<String> identer;
    private List<NyRettighet> aapRettigheter;
    private List<NyRettighet> ungUfoerRettigheter;
    private List<NyRettighet> tvungenForvaltningRettigheter;
    private List<NyRettighet> fritakMeldekortRettigheter;
    private List<Vedtakshistorikk> vedtakshistorikkListe;

    @Before
    public void setUp() {
        var fnr1 = "01010101010";
        identer = new ArrayList<>(Collections.singletonList(fnr1));

        var nyRettighetAap = NyRettighet.builder()
                .build();
        var nyRettighetUngUfoer = NyRettighet.builder()
                .build();
        var nyRettighetTvungenForvaltning = NyRettighet.builder()
                .build();
        var nyRettighetFritakMeldekort = NyRettighet.builder()
                .build();

        aapRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetAap));
        ungUfoerRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetUngUfoer));
        tvungenForvaltningRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetTvungenForvaltning));
        fritakMeldekortRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetFritakMeldekort));

        var vedtakshistorikk = Vedtakshistorikk.builder()
                .aap(aapRettigheter)
                .ungUfoer(ungUfoerRettigheter)
                .tvungenForvaltning(tvungenForvaltningRettigheter)
                .fritakMeldekort(fritakMeldekortRettigheter)
                .build();

        vedtakshistorikkListe = new ArrayList<>((Collections.singletonList(vedtakshistorikk)));

        when(hodejegerenConsumer.getLevende(avspillergruppeId)).thenReturn(identer);
        when(syntetiseringService.hentEksisterendeArbeidsoekerIdenter()).thenReturn(new ArrayList<>(Collections.singletonList(fnr1)));
    }

    @Test
    public void shouldGenerereVedtakshistorikk() {
        var kontonummer = "12131843564";
        var forvalterFnr = "02020202020";
        when(hodejegerenConsumer.getIdenterMedKontonummer(avspillergruppeId, miljoe, antallIdenter, null, null))
                .thenReturn(new ArrayList<>(Collections.singletonList(KontoinfoResponse.builder()
                        .fnr(forvalterFnr)
                        .kontonummer(kontonummer)
                        .build())));
        when(rettighetSyntConsumer.syntetiserVedtakshistorikk(antallIdenter)).thenReturn(vedtakshistorikkListe);

        var nyRettighetAapResponse = NyRettighetResponse.builder()
                .nyeRettigheter(aapRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var nyRettighetUngUfoerResponse = NyRettighetResponse.builder()
                .nyeRettigheter(ungUfoerRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var nyRettighetTvungenForvaltningResponse = NyRettighetResponse.builder()
                .nyeRettigheter(tvungenForvaltningRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var nyRettighetFritakMeldekortResponse = NyRettighetResponse.builder()
                .nyeRettigheter(fritakMeldekortRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var expectedResponsesFromArenaForvalter = new ArrayList<>(
                Arrays.asList(
                        nyRettighetAapResponse,
                        nyRettighetUngUfoerResponse,
                        nyRettighetTvungenForvaltningResponse,
                        nyRettighetFritakMeldekortResponse
                ));

        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetService.genererVedtakshistorikk(avspillergruppeId, miljoe, antallIdenter);

        verify(hodejegerenConsumer).getLevende(avspillergruppeId);
        verify(rettighetSyntConsumer).syntetiserVedtakshistorikk(antallIdenter);
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());

        assertThat(response.size(), equalTo(4));

        assertThat(response.get(0).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(0).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(1).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(1).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(1).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(2).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(2).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(2).getNyeRettigheter().get(0).getForvalter().getGjeldendeKontonr().getKontonr(), equalTo(kontonummer));
        assertThat(response.get(2).getNyeRettigheter().get(0).getForvalter().getUtbetalingsadresse().getFodselsnr(), equalTo(forvalterFnr));
        assertThat(response.get(2).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(3).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(3).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(3).getFeiledeRettigheter().size(), equalTo(0));
    }

    @Test
    public void shouldGenerereAap() {
        var nyRettighetAapResponse = NyRettighetResponse.builder()
                .nyeRettigheter(aapRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var expectedResponsesFromArenaForvalter = new ArrayList<>(Collections.singletonList(nyRettighetAapResponse));

        when(rettighetSyntConsumer.syntetiserRettighetAap(antallIdenter)).thenReturn(aapRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetService.genererAap(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(0).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(0).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));
    }

    @Test
    public void shouldGenerereUngUfoer() {
        var nyRettighetungUfoerResponse = NyRettighetResponse.builder()
                .nyeRettigheter(ungUfoerRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var expectedResponsesFromArenaForvalter = new ArrayList<>(Collections.singletonList(nyRettighetungUfoerResponse));

        when(hodejegerenConsumer.getLevende(avspillergruppeId, MIN_ALDER, MAX_ALDER)).thenReturn(identer);
        when(rettighetSyntConsumer.syntetiserRettighetUngUfoer(antallIdenter)).thenReturn(ungUfoerRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetService.genererUngUfoer(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(0).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(0).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));
    }

    @Test
    public void shouldGenerereTvungenForvaltning() {
        var kontonummer = "12131843564";
        var forvalterFnr = "02020202020";
        when(hodejegerenConsumer.getIdenterMedKontonummer(avspillergruppeId, miljoe, antallIdenter, null, null))
                .thenReturn(new ArrayList<>(Collections.singletonList(KontoinfoResponse.builder()
                        .fnr(forvalterFnr)
                        .kontonummer(kontonummer)
                        .build())));

        var nyRettighetTvungenForvaltningResponse = NyRettighetResponse.builder()
                .nyeRettigheter(tvungenForvaltningRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var expectedResponsesFromArenaForvalter = new ArrayList<>(Collections.singletonList(nyRettighetTvungenForvaltningResponse));

        when(rettighetSyntConsumer.syntetiserRettighetTvungenForvaltning(antallIdenter)).thenReturn(tvungenForvaltningRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetService.genererTvungenForvaltning(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(0).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(0).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(0).getNyeRettigheter().get(0).getForvalter().getGjeldendeKontonr().getKontonr(), equalTo(kontonummer));
        assertThat(response.get(0).getNyeRettigheter().get(0).getForvalter().getUtbetalingsadresse().getFodselsnr(), equalTo(forvalterFnr));
        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));
    }

    @Test
    public void shouldGenerereFritakMeldekort() {
        var nyRettighetFritakMeldekortResponse = NyRettighetResponse.builder()
                .nyeRettigheter(fritakMeldekortRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var expectedResponsesFromArenaForvalter = new ArrayList<>(Collections.singletonList(nyRettighetFritakMeldekortResponse));

        when(rettighetSyntConsumer.syntetiserRettighetFritakMeldekort(antallIdenter)).thenReturn(fritakMeldekortRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetService.genererFritakMeldekort(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(0).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(0).getNyeRettigheter().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));
    }
}