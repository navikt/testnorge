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

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private RettighetSyntConsumer rettighetSyntConsumer;

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
        String fnr1 = "01010101010";
        identer = new ArrayList<>(Collections.singletonList(fnr1));

        NyRettighet nyRettighetAap = NyRettighet.builder()
                .build();
        NyRettighet nyRettighetUngUfoer = NyRettighet.builder()
                .build();
        NyRettighet nyRettighetTvungenForvaltning = NyRettighet.builder()
                .build();
        NyRettighet nyRettighetFritakMeldekort = NyRettighet.builder()
                .build();

        aapRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetAap));
        ungUfoerRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetUngUfoer));
        tvungenForvaltningRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetTvungenForvaltning));
        fritakMeldekortRettigheter = new ArrayList<>(Collections.singletonList(nyRettighetFritakMeldekort));

        Vedtakshistorikk vedtakshistorikk = Vedtakshistorikk.builder()
                .aap(aapRettigheter)
                .ungUfoer(ungUfoerRettigheter)
                .tvungenForvaltning(tvungenForvaltningRettigheter)
                .fritakMeldekort(fritakMeldekortRettigheter)
                .build();

        vedtakshistorikkListe = new ArrayList<>((Collections.singletonList(vedtakshistorikk)));
    }

    @Test
    public void shouldGenerereVedtakshistorikk() {
        when(hodejegerenConsumer.getLevende(avspillergruppeId)).thenReturn(identer);
        when(hodejegerenConsumer.getIdenterMedKontonummer(avspillergruppeId, miljoe, antallIdenter, null, null))
                .thenReturn(new ArrayList<>(Collections.singletonList(KontoinfoResponse.builder()
                        .fnr("02020202020")
                        .kontonummer("12131843564")
                        .build())));
        when(rettighetSyntConsumer.syntetiserVedtakshistorikk(antallIdenter)).thenReturn(vedtakshistorikkListe);

        NyRettighetResponse nyRettighetAapResponse = NyRettighetResponse.builder()
                .nyeRettigheter(aapRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        NyRettighetResponse nyRettighetUngUfoerResponse = NyRettighetResponse.builder()
                .nyeRettigheter(ungUfoerRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        NyRettighetResponse nyRettighetTvungenForvaltningResponse = NyRettighetResponse.builder()
                .nyeRettigheter(tvungenForvaltningRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        NyRettighetResponse nyRettighetFritakMeldekortResponse = NyRettighetResponse.builder()
                .nyeRettigheter(fritakMeldekortRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        List<NyRettighetResponse> expectedResponsesFromArenaForvalter = new ArrayList<>(
                Arrays.asList(
                        nyRettighetAapResponse,
                        nyRettighetUngUfoerResponse,
                        nyRettighetTvungenForvaltningResponse,
                        nyRettighetFritakMeldekortResponse
                ));

        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        List<NyRettighetResponse> response = rettighetService.genererVedtakshistorikk(avspillergruppeId, miljoe, antallIdenter);

        verify(hodejegerenConsumer).getLevende(avspillergruppeId);
        verify(rettighetSyntConsumer).syntetiserVedtakshistorikk(antallIdenter);
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
        assertThat(response.size(), equalTo(4));
        assertThat(response.get(0).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));
        assertThat(response.get(1).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(1).getFeiledeRettigheter().size(), equalTo(0));
        assertThat(response.get(2).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(2).getFeiledeRettigheter().size(), equalTo(0));
        assertThat(response.get(3).getNyeRettigheter().size(), equalTo(1));
        assertThat(response.get(3).getFeiledeRettigheter().size(), equalTo(0));
    }
}