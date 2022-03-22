package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.datoErInnenforPeriode;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DatoUtilsTest {

    @Test
    public void shouldCheckIfDatoErInnenforPeriode() {
        var dagensDato = LocalDate.now();
        var periodeASlutt = LocalDate.now().plusDays(7);
        var periodeBSlutt = LocalDate.now().minusDays(7);

        var periodeStartFoer = LocalDate.now().minusDays(7);

        assertThat(datoErInnenforPeriode(dagensDato, dagensDato, periodeASlutt)).isTrue();
        assertThat(datoErInnenforPeriode(dagensDato, dagensDato, null)).isTrue();
        assertThat(datoErInnenforPeriode(dagensDato, dagensDato, periodeBSlutt)).isFalse();
        assertThat(datoErInnenforPeriode(dagensDato, periodeStartFoer, dagensDato)).isFalse();
        assertThat(datoErInnenforPeriode(dagensDato, periodeStartFoer, dagensDato)).isFalse();
        assertThat(datoErInnenforPeriode(dagensDato, periodeStartFoer, periodeASlutt)).isTrue();

    }

    @Test
    public void shouldSetDatoPeriodeVedtakInnenforMaxAntallMaaneder() {
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

        setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedUgyldigTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);
        setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedGyldigTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);
        setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtakMedNullTilDato, SYKEPENGEERSTATNING_MAKS_PERIODE);

        assertThat(vedtakMedUgyldigTilDato.getTilDato()).isEqualTo(tilDatoEtterEndring);
        assertThat(vedtakMedGyldigTilDato.getTilDato()).isEqualTo(gyldigTilDato);
        assertThat(vedtakMedNullTilDato.getTilDato()).isNull();
    }
}
