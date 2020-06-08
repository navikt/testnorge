package no.nav.registre.arena.core.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.Saksopplysning;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.arena.core.consumer.rs.AapSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@RunWith(MockitoJUnitRunner.class)
public class VedtakshistorikkServiceTest {

    @Mock
    private AapSyntConsumer aapSyntConsumer;

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @Mock
    private RettighetAapService rettighetAapService;

    @InjectMocks
    private VedtakshistorikkService vedtakshistorikkService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallIdenter = 1;
    private String fnr1 = "270699494213";
    private List<String> identer;
    private List<Vedtakshistorikk> vedtakshistorikkListe;
    private List<NyttVedtakAap> aapRettigheter;
    private List<NyttVedtakAap> ungUfoerRettigheter;
    private List<NyttVedtakAap> tvungenForvaltningRettigheter;
    private List<NyttVedtakAap> fritakMeldekortRettigheter;

    @Before
    public void setUp() {
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

        when(serviceUtils.getUtvalgteIdenterIAldersgruppe(eq(avspillergruppeId), eq(antallIdenter), anyInt(), anyInt(), eq(miljoe))).thenReturn(identer);
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
        Map<String, List<NyttVedtakResponse>> responseAsMap = new HashMap<>();
        responseAsMap.put(fnr1, expectedResponsesFromArenaForvalter);

        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(responseAsMap);

        var response = vedtakshistorikkService.genererVedtakshistorikk(avspillergruppeId, miljoe, antallIdenter);

        verify(serviceUtils).getUtvalgteIdenterIAldersgruppe(eq(avspillergruppeId), eq(1), anyInt(), anyInt(), eq(miljoe));
        verify(aapSyntConsumer).syntetiserVedtakshistorikk(antallIdenter);
        verify(rettighetAapService).opprettPersonOgInntektIPopp(anyString(), anyString(), any(NyttVedtakAap.class));
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());

        assertThat(response.get(fnr1).size(), equalTo(4));

        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(0).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(fnr1).get(1).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(1).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(1).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().get(0).getForvalter().getGjeldendeKontonr().getKontonr(), equalTo(kontonummer));
        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().get(0).getForvalter().getUtbetalingsadresse().getFodselsnr(), equalTo(forvalterFnr));
        assertThat(response.get(fnr1).get(2).getFeiledeRettigheter().size(), equalTo(0));

        assertThat(response.get(fnr1).get(3).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(3).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(3).getFeiledeRettigheter().size(), equalTo(0));
    }
}