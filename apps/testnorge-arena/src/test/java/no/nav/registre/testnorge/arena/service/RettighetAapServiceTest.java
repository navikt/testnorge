package no.nav.registre.testnorge.arena.service;

import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.UTFALL_JA;
import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.VEDTAK_TYPE_KODE_O;
import static no.nav.registre.testnorge.arena.service.RettighetAapService.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.registre.testnorge.arena.consumer.rs.request.synt.SyntRequest;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.Saksopplysning;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.testnorge.arena.consumer.rs.AapSyntConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.PensjonTestdataFacadeConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataInntekt;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataPerson;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.HttpStatus;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataResponse;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataResponseDetails;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataStatus;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.service.util.DatoUtils;
import no.nav.registre.testnorge.arena.service.util.ArbeidssoekerUtils;
import no.nav.registre.testnorge.arena.service.util.IdenterUtils;
import no.nav.registre.testnorge.arena.service.util.TiltakUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@RunWith(MockitoJUnitRunner.class)
public class RettighetAapServiceTest {

    @Mock
    private ConsumerUtils consumerUtils;

    @Mock
    private AapSyntConsumer aapSyntConsumer;

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private IdenterUtils identerUtils;

    @Mock
    private ArbeidssoekerUtils arbeidssoekerUtils;

    @Mock
    private TiltakUtils tiltakUtils;

    @Mock
    private DatoUtils datoUtils;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @Mock
    private PensjonTestdataFacadeConsumer pensjonTestdataFacadeConsumer;

    @Mock
    private Random rand;

    @InjectMocks
    private RettighetAapService rettighetAapService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private String fnr1 = "270699494213";
    private int antallIdenter = 1;
    private List<String> identer;
    private NyttVedtakAap aap115Rettighet;
    private List<NyttVedtakAap> aapRettigheter;
    private List<NyttVedtakAap> ungUfoerRettigheter;
    private List<NyttVedtakAap> tvungenForvaltningRettigheter;
    private List<NyttVedtakAap> fritakMeldekortRettigheter;
    private List<SyntRequest> syntRequest;

    @Before
    public void setUp() {
        syntRequest = new ArrayList<>(Collections.singletonList(
                SyntRequest.builder()
                        .fraDato(LocalDate.now().toString())
                        .tilDato(LocalDate.now().toString())
                        .utfall(UTFALL_JA)
                        .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                        .vedtakDato(LocalDate.now().toString())
                        .build()
        ));

        identer = new ArrayList<>(Collections.singletonList(fnr1));

        aap115Rettighet = NyttVedtakAap.builder()
                .build();
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

        when(identerUtils.getLevende(avspillergruppeId, miljoe)).thenReturn(identer);
        when(identerUtils.getUtvalgteIdenterIAldersgruppe(eq(avspillergruppeId), eq(antallIdenter), anyInt(), anyInt(), eq(miljoe))).thenReturn(identer);
        when(identerUtils.hentEksisterendeArbeidsoekerIdenter()).thenReturn(new ArrayList<>(Collections.singletonList(fnr1)));
    }

    @Test
    public void shouldGenerereAap() {
        var nyRettighetAapResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(aapRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        Map<String, List<NyttVedtakResponse>> expectedResponsesFromArenaForvalter = new HashMap<>();
        expectedResponsesFromArenaForvalter.put(fnr1, new ArrayList<>(Collections.singletonList(nyRettighetAapResponse)));

        when(consumerUtils.createSyntRequest(any(LocalDate.class), any(LocalDate.class))).thenReturn(syntRequest);
        when(consumerUtils.createSyntRequest(antallIdenter)).thenReturn(syntRequest);
        when(aapSyntConsumer.syntetiserRettighetAap115(syntRequest)).thenReturn(new ArrayList<>(Collections.singletonList(aap115Rettighet)));
        when(aapSyntConsumer.syntetiserRettighetAap(syntRequest)).thenReturn(aapRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);
        when(pensjonTestdataFacadeConsumer.opprettPerson(any(PensjonTestdataPerson.class))).thenReturn(PensjonTestdataResponse.builder()
                .status(Collections.singletonList(PensjonTestdataStatus.builder()
                        .miljo(miljoe)
                        .response(PensjonTestdataResponseDetails.builder()
                                .httpStatus(HttpStatus.builder()
                                        .status(200)
                                        .reasonPhrase("OK")
                                        .build())
                                .build())
                        .build()))
                .build());
        when(pensjonTestdataFacadeConsumer.opprettInntekt(any(PensjonTestdataInntekt.class))).thenReturn(PensjonTestdataResponse.builder()
                .status(Collections.singletonList(PensjonTestdataStatus.builder()
                        .miljo(miljoe)
                        .response(PensjonTestdataResponseDetails.builder()
                                .httpStatus(HttpStatus.builder()
                                        .status(200)
                                        .reasonPhrase("OK")
                                        .build())
                                .build())
                        .build()))
                .build());

        var response = rettighetAapService.genererAapMedTilhoerende115(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(0).getFeiledeRettigheter().size(), equalTo(0));

        verify(serviceUtils).lagreIHodejegeren(any());
    }

    @Test
    public void shouldGenerereUngUfoer() {
        var nyRettighetungUfoerResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(ungUfoerRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        Map<String, List<NyttVedtakResponse>> expectedResponsesFromArenaForvalter = new HashMap<>();
        expectedResponsesFromArenaForvalter.put(fnr1, new ArrayList<>(Collections.singletonList(nyRettighetungUfoerResponse)));

        when(identerUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallIdenter, 18, 35, miljoe)).thenReturn(identer);
        when(consumerUtils.createSyntRequest(antallIdenter, ARENA_AAP_UNG_UFOER_DATE_LIMIT)).thenReturn(syntRequest);
        when(aapSyntConsumer.syntetiserRettighetUngUfoer(syntRequest)).thenReturn(ungUfoerRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetAapService.genererUngUfoer(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(0).getFeiledeRettigheter().size(), equalTo(0));
    }

    @Test
    public void shouldGenerereTvungenForvaltning() {
        var kontonummer = "12131843564";
        var forvalterFnr = "02020202020";
        when(identerUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallIdenter))
                .thenReturn(new ArrayList<>(Collections.singletonList(KontoinfoResponse.builder()
                        .fnr(forvalterFnr)
                        .kontonummer(kontonummer)
                        .build())));

        var nyRettighetTvungenForvaltningResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(tvungenForvaltningRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        Map<String, List<NyttVedtakResponse>> expectedResponsesFromArenaForvalter = new HashMap<>();
        expectedResponsesFromArenaForvalter.put(fnr1, new ArrayList<>(Collections.singletonList(nyRettighetTvungenForvaltningResponse)));

        when(consumerUtils.createSyntRequest(antallIdenter)).thenReturn(syntRequest);
        when(aapSyntConsumer.syntetiserRettighetTvungenForvaltning(syntRequest)).thenReturn(tvungenForvaltningRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetAapService.genererTvungenForvaltning(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getForvalter().getGjeldendeKontonr().getKontonr(), equalTo(kontonummer));
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getForvalter().getUtbetalingsadresse().getFodselsnr(), equalTo(forvalterFnr));
        assertThat(response.get(fnr1).get(0).getFeiledeRettigheter().size(), equalTo(0));
    }

    @Test
    public void shouldGenerereFritakMeldekort() {
        var nyRettighetFritakMeldekortResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(fritakMeldekortRettigheter)
                .feiledeRettigheter(new ArrayList<>())
                .build();
        Map<String, List<NyttVedtakResponse>> expectedResponsesFromArenaForvalter = new HashMap<>();
        expectedResponsesFromArenaForvalter.put(fnr1, new ArrayList<>(Collections.singletonList(nyRettighetFritakMeldekortResponse)));

        when(identerUtils.hentEksisterendeArbeidsoekerIdenter()).thenReturn(identer);
        when(identerUtils.getLevende(avspillergruppeId, miljoe)).thenReturn(identer);
        when(consumerUtils.createSyntRequest(antallIdenter)).thenReturn(syntRequest);
        when(aapSyntConsumer.syntetiserRettighetFritakMeldekort(syntRequest)).thenReturn(fritakMeldekortRettigheter);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(expectedResponsesFromArenaForvalter);

        var response = rettighetAapService.genererFritakMeldekort(avspillergruppeId, miljoe, antallIdenter);

        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(fnr1).get(0).getNyeRettigheterAap().get(0).getBegrunnelse(), equalTo("Syntetisert rettighet"));
        assertThat(response.get(fnr1).get(0).getFeiledeRettigheter().size(), equalTo(0));
    }
}