package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArenaTiltakServiceTest {

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private RequestUtils requestUtils;

    @Mock
    private ArenaForvalterService arenaForvalterService;

    @InjectMocks
    private ArenaTiltakService arenaTiltakService;

    private NyttVedtakTiltak tiltaksdeltakelse;
    private NyttVedtakTiltak tiltakspenger;
    private NyttVedtakTiltak tiltak;
    private String fnr1 = "12345678910";
    private String miljoe = "test";

    @Before
    public void setUp() {
        tiltaksdeltakelse = NyttVedtakTiltak.builder()
                .tiltakAdminKode("AMO")
                .tiltakYtelse("J")
                .tiltakKode("AMO")
                .build();
        tiltaksdeltakelse.setFraDato(LocalDate.now());
        tiltaksdeltakelse.setTilDato(LocalDate.now().plusMonths(3));

        tiltak = NyttVedtakTiltak.builder()
                .tiltakId(1)
                .tiltakProsentDeltid(100.00)
                .tiltakYtelse("J")
                .build();
        tiltak.setFraDato(LocalDate.now());
        tiltak.setTilDato(LocalDate.now().plusMonths(3));

        tiltakspenger = NyttVedtakTiltak.builder()
                .build();
        tiltakspenger.setVedtaktype("O");
        tiltakspenger.setFraDato(LocalDate.now());
        tiltakspenger.setTilDato(LocalDate.now().plusMonths(3));
    }


    @Test
    public void shouldFilterOutTiltaksdeltakelseWithoutTiltak() {
        when(arenaForvalterService.opprettArbeidssoekerTiltaksdeltakelse(
                fnr1, miljoe, tiltaksdeltakelse.getRettighetType(), LocalDate.now())).thenReturn(Kvalifiseringsgrupper.BATT);

        when(arenaForvalterService.finnTiltak(fnr1, miljoe, tiltaksdeltakelse)).thenReturn(null);

        var historikk = Vedtakshistorikk.builder()
                .tiltaksdeltakelse(Collections.singletonList(tiltaksdeltakelse))
                .build();
        arenaTiltakService.oppdaterTiltaksdeltakelse(historikk, fnr1, miljoe, Collections.emptyList(), tiltaksdeltakelse, LocalDate.now());

        assertThat(historikk.getTiltaksdeltakelse()).isEmpty();
    }

    @Test
    public void shouldUpdateTiltaksdeltakelse() {
        when(arenaForvalterService.opprettArbeidssoekerTiltaksdeltakelse(
                fnr1, miljoe, tiltaksdeltakelse.getRettighetType(), LocalDate.now())).thenReturn(Kvalifiseringsgrupper.BATT);

        when(arenaForvalterService.finnTiltak(fnr1, miljoe, tiltaksdeltakelse)).thenReturn(tiltak);

        var historikk = Vedtakshistorikk.builder()
                .tiltaksdeltakelse(Collections.singletonList(tiltaksdeltakelse))
                .build();
        List<NyttVedtakTiltak> tiltaksliste = new ArrayList<>();
        arenaTiltakService.oppdaterTiltaksdeltakelse(historikk, fnr1, miljoe, tiltaksliste, tiltaksdeltakelse, LocalDate.now());

        assertThat(historikk.getTiltaksdeltakelse()).hasSize(1);
        assertThat(historikk.getTiltaksdeltakelse().get(0).getTiltakId()).isEqualTo(1);
        assertThat(tiltaksliste).hasSize(1);
        assertThat(tiltaksliste.get(0).getTiltakId()).isEqualTo(1);
    }

    @Test
    public void shouldOpprettTiltakspenger() {
        var historikk = Vedtakshistorikk.builder()
                .tiltaksdeltakelse(Collections.singletonList(tiltaksdeltakelse))
                .tiltakspenger(Arrays.asList(tiltakspenger, tiltakspenger))
                .build();

        List<RettighetRequest> rettigheter = new ArrayList<>();
        arenaTiltakService.opprettVedtakTiltakspenger(historikk, fnr1, miljoe, rettigheter);

        assertThat(rettigheter).hasSize(1);
        assertThat(historikk.getTiltakspenger()).hasSize(1);
    }

    @Test
    public void shouldNotOpprettTiltakspenger() {
        var historikk = Vedtakshistorikk.builder()
                .tiltakspenger(Arrays.asList(tiltakspenger, tiltakspenger))
                .build();

        List<RettighetRequest> rettigheter = new ArrayList<>();
        arenaTiltakService.opprettVedtakTiltakspenger(historikk, fnr1, miljoe, rettigheter);

        assertThat(rettigheter).isEmpty();
        assertThat(historikk.getTiltakspenger()).isEmpty();
    }

    @Test
    public void shouldRemoveOverlappingVedtak(){
        var aap1 = NyttVedtakAap.builder().build();
        aap1.setFraDato(LocalDate.now());
        aap1.setTilDato(LocalDate.now().plusMonths(3));
        aap1.setVedtaktype("O");

        var aap2 = NyttVedtakAap.builder().build();
        aap2.setFraDato(LocalDate.now().minusMonths(6));
        aap2.setTilDato(LocalDate.now().minusMonths(3));
        aap2.setVedtaktype("O");


        var tiltak1 = NyttVedtakTiltak.builder().build();
        tiltak1.setFraDato(LocalDate.now());
        tiltak1.setTilDato(LocalDate.now().plusMonths(3));
        tiltak1.setTiltakProsentDeltid(60.00);

        var tiltak2 =  NyttVedtakTiltak.builder().build();
        tiltak2.setFraDato(LocalDate.now().plusMonths(3));
        tiltak2.setTilDato(LocalDate.now().plusMonths(4));
        tiltak2.setTiltakProsentDeltid(100.00);

        var response1 = arenaTiltakService.removeOverlappingTiltakVedtak(Arrays.asList(tiltak1), Arrays.asList(aap1));
        var response2 = arenaTiltakService.removeOverlappingTiltakVedtak(Arrays.asList(tiltak1, tiltak2), Arrays.asList(aap2));

        assertThat(response1).isEmpty();
        assertThat(response2).hasSize(1);
        assertThat(response2.get(0).getTiltakProsentDeltid()).isEqualTo(60.0);
    }
}
