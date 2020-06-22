package no.nav.registre.arena.core.service.util;

import java.time.LocalDate;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static no.nav.registre.arena.core.service.RettighetAapService.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ServiceUtilsTest {

    @InjectMocks
    private ServiceUtils serviceUtils;

    @Test
    public void shouldSetDatoPeriodeVedtakInnenforMaxAntallMaaneder(){
        var ugyldigTilDato = LocalDate.now().plusMonths(SYKEPENGEERSTATNING_MAKS_PERIODE + 1);
        var gyldigTilDato = LocalDate.now().plusMonths(SYKEPENGEERSTATNING_MAKS_PERIODE - 1);
        var tilDatoEtterEndring = LocalDate.now().plusMonths(6);

        var vedtakMedUgyldigTilDato = new NyttVedtakAap();
        vedtakMedUgyldigTilDato.setFraDato(LocalDate.now());
        vedtakMedUgyldigTilDato.setTilDato(ugyldigTilDato);

        var vedtakMedGyldigTilDato = new NyttVedtakAap();
        vedtakMedGyldigTilDato.setFraDato(LocalDate.now());
        vedtakMedGyldigTilDato.setTilDato(gyldigTilDato);

        var vedtakMedNullTilDato = new NyttVedtakAap();
        vedtakMedNullTilDato.setFraDato(LocalDate.now());
        vedtakMedNullTilDato.setTilDato(null);

        serviceUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedUgyldigTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);
        serviceUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedGyldigTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);
        serviceUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedNullTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);

        assertThat(vedtakMedUgyldigTilDato.getTilDato()).isEqualTo(tilDatoEtterEndring);
        assertThat(vedtakMedGyldigTilDato.getTilDato()).isEqualTo(gyldigTilDato);
        assertThat(vedtakMedNullTilDato.getTilDato()).isNull();
    }
}
