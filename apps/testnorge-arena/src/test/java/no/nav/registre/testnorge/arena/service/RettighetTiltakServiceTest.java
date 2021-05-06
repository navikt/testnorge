package no.nav.registre.testnorge.arena.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.service.util.KodeMedSannsynlighet;
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
import static org.mockito.Mockito.when;

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
    private IdentService identService;

    @Mock
    private ArbeidssoekerService arbeidssoekerService;

    @Mock
    private Random rand;

    @InjectMocks
    private RettighetTiltakService rettighetTiltakService;

    @Before
    public void setUp() {
        List<String> identer = new ArrayList<>(Collections.singletonList("01010101010"));

        Map<String, List<NyttVedtakResponse>> response = new HashMap<>();
        response.put(identer.get(0), Collections.singletonList(new NyttVedtakResponse()));
        response.get(identer.get(0)).get(0).setFeiledeRettigheter(new ArrayList<>());

        NyttVedtakTiltak tiltakMedTilDatoFremITid = NyttVedtakTiltak.builder()
                .tiltakId(123)
                .tiltakAdminKode("IND")
                .tiltakskarakteristikk("IND")
                .deltakerstatusKode("GJENN")
                .build();
        tiltakMedTilDatoFremITid.setFraDato(LocalDate.now().minusMonths(1));
        tiltakMedTilDatoFremITid.setTilDato(LocalDate.now().plusMonths(1));

        NyttVedtakTiltak tiltakMedTilDatoLikDagens = NyttVedtakTiltak.builder()
                .tiltakId(123)
                .tiltakAdminKode("IND")
                .tiltakskarakteristikk("IND")
                .deltakerstatusKode("GJENN")
                .build();
        tiltakMedTilDatoLikDagens.setFraDato(LocalDate.now().minusMonths(1));
        tiltakMedTilDatoLikDagens.setTilDato(LocalDate.now());

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

        var request = rettighetTiltakService.opprettRettighetTiltaksaktivitetRequest("", "", nyttVedtakTillegg);

        assertThat(request.getVedtakTiltak()).hasSize(1);
        assertThat(request.getVedtakTiltak().get(0).getFraDato()).isEqualTo(LocalDate.now().minusMonths(1));
        assertThat(request.getVedtakTiltak().get(0).getTilDato()).isEqualTo(LocalDate.now());
        assertThat(request.getVedtakTiltak().get(0).getAktivitetStatuskode()).isEqualTo(aktivitetskodeMedSannsynlighet.getKode());
        assertThat(request.getVedtakTiltak().get(0).getAktivitetkode()).isEqualTo(aktivitetskodeMedSannsynlighet.getKode());

    }


}
