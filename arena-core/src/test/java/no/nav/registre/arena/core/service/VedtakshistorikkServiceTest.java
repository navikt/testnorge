package no.nav.registre.arena.core.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.AapSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.Saksopplysning;
import no.nav.registre.arena.domain.historikk.Vedtakshistorikk;
import no.nav.registre.arena.domain.vedtak.NyttVedtakAap;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@RunWith(MockitoJUnitRunner.class)
public class VedtakshistorikkServiceTest {

    @Mock
    private AapSyntConsumer aapSyntConsumer;

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @InjectMocks
    private VedtakshistorikkService vedtakshistorikkService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallIdenter = 1;
    private List<String> identer;
    private List<Vedtakshistorikk> vedtakshistorikkListe;
    private List<NyttVedtakAap> aapRettigheter;
    private List<NyttVedtakAap> ungUfoerRettigheter;
    private List<NyttVedtakAap> tvungenForvaltningRettigheter;
    private List<NyttVedtakAap> fritakMeldekortRettigheter;

    @Before
    public void setUp() {
        var fnr1 = "270699494213";
        identer = new ArrayList<>(Collections.singletonList(fnr1));

        Saksopplysning saksopplysning = new Saksopplysning();
        var nyRettighetAap = NyttVedtakAap.builder()
                .build();
        nyRettighetAap.setFraDato(LocalDate.now().minusDays(7));
        nyRettighetAap.setTilDato(LocalDate.now());
        nyRettighetAap.setGenSaksopplysninger(Collections.singletonList(saksopplysning));
        var nyRettighetUngUfoer = NyttVedtakAap.builder()
                .build();
        nyRettighetUngUfoer.setFraDato(LocalDate.now().minusDays(7));
        var nyRettighetTvungenForvaltning = NyttVedtakAap.builder()
                .build();
        var nyRettighetFritakMeldekort = NyttVedtakAap.builder()
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

        when(serviceUtils.getUtvalgteIdenterIAldersgruppe(eq(avspillergruppeId), eq(antallIdenter), anyInt(), anyInt())).thenReturn(identer);
    }

    @Test
    public void shouldGenerereVedtakshistorikk() {
        var kontonummer = "12131843564";
        var forvalterFnr = "02020202020";
        when(serviceUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallIdenter))
                .thenReturn(new ArrayList<>(Collections.singletonList(KontoinfoResponse.builder()
                        .fnr(forvalterFnr)
                        .kontonummer(kontonummer)
                        .build())));
        when(aapSyntConsumer.syntetiserVedtakshistorikk(antallIdenter)).thenReturn(vedtakshistorikkListe);

        var nyRettighetAapResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(aapRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var nyRettighetUngUfoerResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(ungUfoerRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var nyRettighetTvungenForvaltningResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(tvungenForvaltningRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        var nyRettighetFritakMeldekortResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(fritakMeldekortRettigheter)
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

        var response = vedtakshistorikkService.genererVedtakshistorikk(avspillergruppeId, miljoe, antallIdenter);

        verify(serviceUtils).getUtvalgteIdenterIAldersgruppe(eq(avspillergruppeId), eq(1), anyInt(), anyInt());
        verify(aapSyntConsumer).syntetiserVedtakshistorikk(antallIdenter);
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());

        assertThat(response.size(), equalTo(4));

        assertThat(response.get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(0).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(1).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(1).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(1).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(2).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(2).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(2).getNyeRettigheterAap().get(0).getForvalter().getGjeldendeKontonr().getKontonr(), equalTo(kontonummer));
        assertThat(response.get(2).getNyeRettigheterAap().get(0).getForvalter().getUtbetalingsadresse().getFodselsnr(), equalTo(forvalterFnr));
        assertThat(response.get(2).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(3).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(3).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(3).getFeiledeRettigheter().size(), equalTo(0));
    }
}