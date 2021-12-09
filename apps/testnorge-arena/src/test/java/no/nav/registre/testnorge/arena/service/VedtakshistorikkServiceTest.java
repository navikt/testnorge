package no.nav.registre.testnorge.arena.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.ARENA_AAP_UNG_UFOER_DATE_LIMIT;

import no.nav.registre.testnorge.arena.consumer.rs.SyntVedtakshistorikkConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.service.util.RequestUtils;
import no.nav.registre.testnorge.arena.service.util.TilleggUtils;
import no.nav.registre.testnorge.arena.service.util.TiltakUtils;
import no.nav.testnav.libs.domain.dto.arena.testnorge.aap.gensaksopplysninger.Saksopplysning;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

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

import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@RunWith(MockitoJUnitRunner.class)
public class VedtakshistorikkServiceTest {

    @Mock
    private SyntVedtakshistorikkConsumer syntVedtakshistorikkConsumer;
    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @Mock
    private IdentService identService;
    @Mock
    private PensjonService pensjonService;
    @Mock
    private ArenaBrukerService arenaBrukerService;

    @Mock
    private RequestUtils requestUtils;
    @Mock
    private TilleggUtils tilleggUtils;
    @Mock
    private TiltakUtils tiltakUtils;

    @InjectMocks
    private VedtakshistorikkService vedtakshistorikkService;

    private final Long avspillergruppeId = 123L;
    private final String miljoe = "t1";
    private final int antallIdenter = 1;
    private final String fnr1 = "270699494213";
    private List<Vedtakshistorikk> vedtakshistorikkListe;
    private List<NyttVedtakAap> aapRettigheter;
    private List<NyttVedtakAap> ungUfoerRettigheter;
    private List<NyttVedtakAap> tvungenForvaltningRettigheter;
    private List<NyttVedtakAap> fritakMeldekortRettigheter;

    @Before
    public void setUp() {
        List<String> identer = new ArrayList<>(Collections.singletonList(fnr1));

        Saksopplysning saksopplysning = new Saksopplysning();
        var nyRettighetAap = NyttVedtakAap.builder()
                .build();
        nyRettighetAap.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));
        nyRettighetAap.setTilDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT);
        nyRettighetAap.setGenSaksopplysninger(Collections.singletonList(saksopplysning));
        var nyRettighetUngUfoer = NyttVedtakAap.builder()
                .build();
        nyRettighetUngUfoer.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));
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

        when(identService.getUtvalgteIdenterIAldersgruppe(eq(avspillergruppeId), eq(antallIdenter), anyInt(), anyInt(), eq(miljoe), any(LocalDate.class))).thenReturn(identer);
    }

    @Test
    public void shouldGenerereVedtakshistorikk() {
        var kontonummer = "12131843564";
        var forvalterFnr = "02020202020";
        when(identService.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallIdenter))
                .thenReturn(new ArrayList<>(Collections.singletonList(KontoinfoResponse.builder()
                        .fnr(forvalterFnr)
                        .kontonummer(kontonummer)
                        .build())));
        when(syntVedtakshistorikkConsumer.syntetiserVedtakshistorikk(antallIdenter)).thenReturn(vedtakshistorikkListe);

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
        var expectedResponsesFromArenaForvalter = Arrays.asList(
                nyRettighetAapResponse,
                nyRettighetUngUfoerResponse,
                nyRettighetTvungenForvaltningResponse,
                nyRettighetFritakMeldekortResponse
        );
        Map<String, List<NyttVedtakResponse>> responseAsMap = new HashMap<>();
        responseAsMap.put(fnr1, expectedResponsesFromArenaForvalter);

        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(responseAsMap);
        when(pensjonService.opprettetPersonOgInntektIPopp(anyString(), anyString(), any(LocalDate.class))).thenReturn(true);

        var response = vedtakshistorikkService.genererVedtakshistorikk(avspillergruppeId, miljoe, antallIdenter);

        verify(identService).getUtvalgteIdenterIAldersgruppe(eq(avspillergruppeId), eq(antallIdenter), anyInt(), anyInt(), eq(miljoe), eq(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7)));
        verify(syntVedtakshistorikkConsumer).syntetiserVedtakshistorikk(antallIdenter);
        verify(pensjonService).opprettetPersonOgInntektIPopp(anyString(), anyString(), any(LocalDate.class));
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());

        assertThat(response.get(fnr1)).hasSize(4);

        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap()).hasSize(1);
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getBegrunnelse()).isEqualTo("Syntetisert rettighet");
        assertThat(response.get(fnr1).get(0).getFeiledeRettigheter()).isEmpty();

        assertThat(response.get(fnr1).get(1).getNyeRettigheterAap()).hasSize(1);
        assertThat(response.get(fnr1).get(1).getNyeRettigheterAap().get(0).getBegrunnelse()).isEqualTo("Syntetisert rettighet");
        assertThat(response.get(fnr1).get(1).getFeiledeRettigheter()).isEmpty();

        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().size()).isEqualTo(1);
        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().get(0).getBegrunnelse()).isEqualTo("Syntetisert rettighet");
        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().get(0).getForvalter().getGjeldendeKontonr().getKontonr()).isEqualTo(kontonummer);
        assertThat(response.get(fnr1).get(2).getNyeRettigheterAap().get(0).getForvalter().getUtbetalingsadresse().getFodselsnr()).isEqualTo(forvalterFnr);
        assertThat(response.get(fnr1).get(2).getFeiledeRettigheter()).isEmpty();

        assertThat(response.get(fnr1).get(3).getNyeRettigheterAap()).hasSize(1);
        assertThat(response.get(fnr1).get(3).getNyeRettigheterAap().get(0).getBegrunnelse()).isEqualTo("Syntetisert rettighet");
        assertThat(response.get(fnr1).get(3).getFeiledeRettigheter()).isEmpty();
    }
}