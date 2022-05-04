package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
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

    private NyttVedtakTiltak tiltaksdeltakelse1;
    private NyttVedtakTiltak tiltakspenger;
    private NyttVedtakTiltak tiltak;
    private String fnr1 = "12345678910";
    private String miljoe = "test";

    @Before
    public void setUp() {
        tiltaksdeltakelse1 = NyttVedtakTiltak.builder()
                .tiltakAdminKode("AMO")
                .tiltakYtelse("J")
                .tiltakKode("AMO")
                .build();
        tiltaksdeltakelse1.setFraDato(LocalDate.now());
        tiltaksdeltakelse1.setTilDato(LocalDate.now().plusMonths(3));

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
    public void shouldFilterOutTiltaksdeltakelseWithoutTiltak(){
        when(arenaForvalterService.opprettArbeidssoekerTiltaksdeltakelse(
                fnr1, miljoe, tiltaksdeltakelse1.getRettighetType(), LocalDate.now())).thenReturn(Kvalifiseringsgrupper.BATT);

        when(arenaForvalterService.finnTiltak(fnr1, miljoe, tiltaksdeltakelse1)).thenReturn(null);

        var historikk = Vedtakshistorikk.builder()
                .tiltaksdeltakelse(Collections.singletonList(tiltaksdeltakelse1))
                .build();
        arenaTiltakService.oppdaterTiltaksdeltakelse(historikk, fnr1, miljoe, Collections.emptyList(), tiltaksdeltakelse1, LocalDate.now());

        assertThat(historikk.getTiltaksdeltakelse()).hasSize(0);
    }

    @Test
    public void shouldUpdateTiltaksdeltakelse(){
        when(arenaForvalterService.opprettArbeidssoekerTiltaksdeltakelse(
                fnr1, miljoe, tiltaksdeltakelse1.getRettighetType(), LocalDate.now())).thenReturn(Kvalifiseringsgrupper.BATT);

        when(arenaForvalterService.finnTiltak(fnr1, miljoe, tiltaksdeltakelse1)).thenReturn(tiltak);

        var historikk = Vedtakshistorikk.builder()
                .tiltaksdeltakelse(Collections.singletonList(tiltaksdeltakelse1))
                .build();
        List<NyttVedtakTiltak> tiltaksliste = new ArrayList<>();
        arenaTiltakService.oppdaterTiltaksdeltakelse(historikk, fnr1, miljoe, tiltaksliste, tiltaksdeltakelse1, LocalDate.now());

        assertThat(historikk.getTiltaksdeltakelse()).hasSize(1);
        assertThat(historikk.getTiltaksdeltakelse().get(0).getTiltakId()).isEqualTo(1);
        assertThat(tiltaksliste).hasSize(1);
        assertThat(tiltaksliste.get(0).getTiltakId()).isEqualTo(1);
    }

    @Test
    public void shouldOpprettTiltakspenger(){
        var historikk = Vedtakshistorikk.builder()
                .tiltaksdeltakelse(Collections.singletonList(tiltaksdeltakelse1))
                .tiltakspenger(Arrays.asList(tiltakspenger, tiltakspenger))
                .build();

        List<RettighetRequest > rettigheter = new ArrayList<>();
        arenaTiltakService.opprettVedtakTiltakspenger(historikk, fnr1, miljoe, rettigheter);

        assertThat(rettigheter).hasSize(1);
        assertThat(historikk.getTiltakspenger()).hasSize(1);
    }
}
