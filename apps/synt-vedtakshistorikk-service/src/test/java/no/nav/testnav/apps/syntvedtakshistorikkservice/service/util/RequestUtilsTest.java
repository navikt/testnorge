package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetTilleggRequest;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestUtilsTest {

    @Mock
    private ServiceUtils serviceUtils;

    @InjectMocks
    private RequestUtils requestUtils;

    @Test
    void shouldOppretteRettighetTiltaksaktivitetRequest() {
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

        var request = requestUtils.getRettighetTiltaksaktivitetRequest("", "", nyttVedtakTillegg.getRettighetKode(), nyttVedtakTillegg.getVedtaksperiode());

        assertThat(request.getVedtakTiltak()).hasSize(1);
        assertThat(request.getVedtakTiltak().get(0).getFraDato()).isEqualTo(LocalDate.now().minusMonths(1));
        assertThat(request.getVedtakTiltak().get(0).getTilDato()).isEqualTo(LocalDate.now());
        assertThat(request.getVedtakTiltak().get(0).getAktivitetStatuskode()).isEqualTo("BEHOV");
        assertThat(request.getVedtakTiltak().get(0).getAktivitetkode()).isEqualTo(aktivitetskodeMedSannsynlighet.getKode());

    }

}
