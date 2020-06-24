package no.nav.registre.arena.core.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.core.service.util.KodeMedSannsynlighet;
import no.nav.registre.arena.core.service.util.ServiceUtils;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import static no.nav.registre.arena.core.service.RettighetTiltakService.aktivitetstatuskodeFullfoert;

@RunWith(MockitoJUnitRunner.class)
public class RettighetTiltakServiceTest {

    @Mock
    private TiltakSyntConsumer tiltakSyntConsumer;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @Mock
    private ServiceUtils serviceUtils;

    @InjectMocks
    private RettighetTiltakService rettighetTiltakService;

    private Long avspillergruppeId = 1L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 1;
    private List<String> identer;
    private Map<String, List<NyttVedtakResponse>> response;
    private List<NyttVedtakTiltak> vedtak;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Collections.singletonList("01010101010"));

        response = new HashMap<>();
        response.put(identer.get(0),Collections.singletonList(new NyttVedtakResponse()));
        response.get(identer.get(0)).get(0).setFeiledeRettigheter(new ArrayList<>());

        vedtak = new ArrayList<>(Collections.singletonList(new NyttVedtakTiltak()));
        vedtak.get(0).setTiltakskarakteristikk("IND");
        vedtak.get(0).setDeltakerstatusKode("GJENN");
    }


    @Test
    public void shouldOnlyOppretteTiltaksdeltakelse() {
        when(tiltakSyntConsumer.opprettTiltaksdeltakelse(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTiltak()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());
        when(serviceUtils.combineNyttVedtakResponseLists(anyMap(), anyMap())).thenReturn(new HashMap<>());

        rettighetTiltakService.opprettTiltaksdeltakelse(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tiltakSyntConsumer).opprettTiltaksdeltakelse(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
        verify(serviceUtils).combineNyttVedtakResponseLists(anyMap(), anyMap());
    }

    @Test
    public void shouldOppretteTiltaksdeltakelseOgEndreDeltakerstatus() {
        when(tiltakSyntConsumer.opprettTiltaksdeltakelse(antallNyeIdenter)).thenReturn(vedtak);
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(tiltakSyntConsumer.opprettDeltakerstatus(antallNyeIdenter)).thenReturn(vedtak);
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(response);

        rettighetTiltakService.opprettTiltaksdeltakelse(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tiltakSyntConsumer).opprettTiltaksdeltakelse(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(tiltakSyntConsumer).opprettDeltakerstatus(antallNyeIdenter);
        verify(rettighetArenaForvalterConsumer, times(2)).opprettRettighet(anyList());
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

        var aktivitetskodeMedSannsynlighet = KodeMedSannsynlighet.builder().kode("ARBFORB").build();

        when(serviceUtils.velgKodeBasertPaaSannsynlighet(anyList())).thenReturn(aktivitetskodeMedSannsynlighet);

        var request = rettighetTiltakService.opprettRettighetTiltaksaktivitetRequest(tilleggRequest, false);

        assertThat(request.getVedtakTiltak()).hasSize(1);
        assertThat(request.getVedtakTiltak().get(0).getFraDato()).isEqualTo(LocalDate.now().minusMonths(1));
        assertThat(request.getVedtakTiltak().get(0).getTilDato()).isEqualTo(LocalDate.now());
        assertThat(request.getVedtakTiltak().get(0).getAktivitetstatuskode()).isEqualTo(aktivitetstatuskodeFullfoert);
        assertThat(request.getVedtakTiltak().get(0).getAktivitetkode()).isEqualTo(aktivitetskodeMedSannsynlighet.getKode());

    }


}
