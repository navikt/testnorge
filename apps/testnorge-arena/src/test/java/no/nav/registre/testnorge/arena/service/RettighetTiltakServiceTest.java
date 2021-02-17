package no.nav.registre.testnorge.arena.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.arena.service.util.IdenterUtils;
import no.nav.registre.testnorge.arena.service.util.ArbeidssoekerUtils;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.service.util.TiltakUtils;
import no.nav.registre.testnorge.arena.service.util.KodeMedSannsynlighet;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.UTFALL_JA;
import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.VEDTAK_TYPE_KODE_O;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class RettighetTiltakServiceTest {
    @Mock
    private ConsumerUtils consumerUtils;

    @Mock
    private TiltakSyntConsumer tiltakSyntConsumer;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private IdenterUtils identerUtils;

    @Mock
    private TiltakUtils tiltakUtils;

    @Mock
    private ArbeidssoekerUtils arbeidssoekerUtils;

    @InjectMocks
    private RettighetTiltakService rettighetTiltakService;

    private Long avspillergruppeId = 1L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 1;
    private List<String> identer;
    private NyttVedtakTiltak tiltakMedTilDatoFremITid;
    private NyttVedtakTiltak tiltakMedTilDatoLikDagens;
    private List<RettighetSyntRequest> syntRequest;

    @Before
    public void setUp() {
        syntRequest = new ArrayList<>(Collections.singletonList(
                RettighetSyntRequest.builder()
                        .fraDato(LocalDate.now().toString())
                        .tilDato(LocalDate.now().toString())
                        .utfall(UTFALL_JA)
                        .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                        .vedtakDato(LocalDate.now().toString())
                        .build()
        ));
        identer = new ArrayList<>(Collections.singletonList("01010101010"));

        Map<String, List<NyttVedtakResponse>> response = new HashMap<>();
        response.put(identer.get(0), Collections.singletonList(new NyttVedtakResponse()));
        response.get(identer.get(0)).get(0).setFeiledeRettigheter(new ArrayList<>());

        tiltakMedTilDatoFremITid = NyttVedtakTiltak.builder()
                .tiltakId(123)
                .tiltakAdminKode("IND")
                .tiltakskarakteristikk("IND")
                .deltakerstatusKode("GJENN")
                .build();
        tiltakMedTilDatoFremITid.setFraDato(LocalDate.now().minusMonths(1));
        tiltakMedTilDatoFremITid.setTilDato(LocalDate.now().plusMonths(1));

        tiltakMedTilDatoLikDagens = NyttVedtakTiltak.builder()
                .tiltakId(123)
                .tiltakAdminKode("IND")
                .tiltakskarakteristikk("IND")
                .deltakerstatusKode("GJENN")
                .build();
        tiltakMedTilDatoLikDagens.setFraDato(LocalDate.now().minusMonths(1));
        tiltakMedTilDatoLikDagens.setTilDato(LocalDate.now());

    }


    @Test
    public void shouldOnlyOppretteTiltaksdeltakelseOgEndreDeltakerstatusGjennomfoeres() {
        var vedtak = Collections.singletonList(tiltakMedTilDatoFremITid);
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tiltakSyntConsumer.opprettTiltaksdeltakelse(syntRequest)).thenReturn(vedtak);
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());
        when(tiltakUtils.finnTiltak(identer.get(0), miljoe, tiltakMedTilDatoFremITid)).thenReturn(tiltakMedTilDatoFremITid);
        when(tiltakUtils.canSetDeltakelseTilGjennomfoeres(tiltakMedTilDatoFremITid, Collections.singletonList(tiltakMedTilDatoFremITid))).thenReturn(true);
        when(tiltakUtils.getVedtakForTiltaksdeltakelseRequest(tiltakMedTilDatoFremITid)).thenReturn(tiltakMedTilDatoFremITid);
        when(tiltakUtils.getFoersteEndringerDeltakerstatus(anyString())).thenReturn(Collections.singletonList(Deltakerstatuser.GJENN.toString()));
        when(arbeidssoekerUtils.opprettArbeidssoekerTiltak(anyList(), anyString())).thenReturn(Collections.emptyList());

        rettighetTiltakService.opprettTiltaksdeltakelse(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tiltakSyntConsumer).opprettTiltaksdeltakelse(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(rettighetArenaForvalterConsumer, times(2)).opprettRettighet(anyList());
        verify(tiltakUtils).finnTiltak(identer.get(0), miljoe, tiltakMedTilDatoFremITid);
        verify(tiltakUtils).getVedtakForTiltaksdeltakelseRequest(tiltakMedTilDatoFremITid);
        verify(tiltakUtils).getFoersteEndringerDeltakerstatus(anyString());
    }

    @Test
    public void shouldOppretteTiltaksdeltakelseOgEndreDeltakerstatusAvsluttet() {
        var vedtak = Collections.singletonList(tiltakMedTilDatoLikDagens);
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tiltakSyntConsumer.opprettTiltaksdeltakelse(syntRequest)).thenReturn(vedtak);
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());
        when(tiltakUtils.finnTiltak(identer.get(0), miljoe, tiltakMedTilDatoLikDagens)).thenReturn(tiltakMedTilDatoLikDagens);
        when(tiltakUtils.canSetDeltakelseTilGjennomfoeres(tiltakMedTilDatoLikDagens, Collections.singletonList(tiltakMedTilDatoLikDagens))).thenReturn(true);
        when(tiltakUtils.canSetDeltakelseTilFinished(tiltakMedTilDatoLikDagens, Collections.singletonList(tiltakMedTilDatoLikDagens))).thenReturn(true);
        when(tiltakUtils.getVedtakForTiltaksdeltakelseRequest(tiltakMedTilDatoLikDagens)).thenReturn(tiltakMedTilDatoLikDagens);
        when(tiltakUtils.getFoersteEndringerDeltakerstatus(anyString())).thenReturn(new ArrayList<>(Collections.singletonList(Deltakerstatuser.GJENN.toString())));
        when(tiltakUtils.getAvsluttendeDeltakerstatus(tiltakMedTilDatoLikDagens, Collections.singletonList(tiltakMedTilDatoLikDagens))).thenReturn(Deltakerstatuser.FULLF);
        when(arbeidssoekerUtils.opprettArbeidssoekerTiltak(anyList(), anyString())).thenReturn(Collections.emptyList());

        rettighetTiltakService.opprettTiltaksdeltakelse(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tiltakSyntConsumer).opprettTiltaksdeltakelse(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(rettighetArenaForvalterConsumer, times(2)).opprettRettighet(anyList());
        verify(tiltakUtils).finnTiltak(identer.get(0), miljoe, tiltakMedTilDatoLikDagens);
    }

    @Test
    public void shouldOppretteRettighetTiltaksaktivitetRequest() {
        var vedtaksperiode = new Vedtaksperiode();
        vedtaksperiode.setFom(LocalDate.now().minusMonths(1));
        vedtaksperiode.setTom(LocalDate.now());

        var nyttVedtakTillegg = new NyttVedtakTillegg();
        nyttVedtakTillegg.setVedtaksperiode(vedtaksperiode);
        nyttVedtakTillegg.setRettighetKode("TSOFLYTT");

        var tilleggRequest = new RettighetTilleggRequest();
        tilleggRequest.setNyeTilleggsstonad(Collections.singletonList(nyttVedtakTillegg));

        var aktivitetskodeMedSannsynlighet = KodeMedSannsynlighet.builder().kode("TEST").build();

        when(serviceUtils.velgKodeBasertPaaSannsynlighet(anyList())).thenReturn(aktivitetskodeMedSannsynlighet);

        var request = rettighetTiltakService.opprettRettighetTiltaksaktivitetRequest(tilleggRequest, true);

        assertThat(request.getVedtakTiltak()).hasSize(1);
        assertThat(request.getVedtakTiltak().get(0).getFraDato()).isEqualTo(LocalDate.now().minusMonths(1));
        assertThat(request.getVedtakTiltak().get(0).getTilDato()).isEqualTo(LocalDate.now());
        assertThat(request.getVedtakTiltak().get(0).getAktivitetStatuskode()).isEqualTo(aktivitetskodeMedSannsynlighet.getKode());
        assertThat(request.getVedtakTiltak().get(0).getAktivitetkode()).isEqualTo(aktivitetskodeMedSannsynlighet.getKode());

    }


}
