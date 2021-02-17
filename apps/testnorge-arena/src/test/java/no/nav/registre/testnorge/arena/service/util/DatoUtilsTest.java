package no.nav.registre.testnorge.arena.service.util;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static no.nav.registre.testnorge.arena.service.RettighetAapService.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class DatoUtilsTest {

    @InjectMocks
    private DatoUtils datoUtils;

    @Test
    public void shouldCheckIfDatoErInnenforPeriode(){
        var dagensDato = LocalDate.now();
        var periodeASlutt = LocalDate.now().plusDays(7);
        var periodeBSlutt = LocalDate.now().minusDays(7);

        var periodeStartFoer = LocalDate.now().minusDays(7);

        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, dagensDato, periodeASlutt)).isTrue();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, dagensDato, null)).isTrue();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, dagensDato, periodeBSlutt)).isFalse();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, periodeStartFoer, dagensDato)).isFalse();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, periodeStartFoer, dagensDato)).isFalse();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, periodeStartFoer, periodeASlutt)).isTrue();

    }

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

        datoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedUgyldigTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);
        datoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedGyldigTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);
        datoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedNullTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);

        assertThat(vedtakMedUgyldigTilDato.getTilDato()).isEqualTo(tilDatoEtterEndring);
        assertThat(vedtakMedGyldigTilDato.getTilDato()).isEqualTo(gyldigTilDato);
        assertThat(vedtakMedNullTilDato.getTilDato()).isNull();
    }
}
