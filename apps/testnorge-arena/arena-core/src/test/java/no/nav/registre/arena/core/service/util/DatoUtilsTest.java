package no.nav.registre.arena.core.service.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
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
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, dagensDato, dagensDato)).isFalse();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, dagensDato, periodeBSlutt)).isFalse();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, periodeStartFoer, dagensDato)).isFalse();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, periodeStartFoer, dagensDato)).isFalse();
        assertThat(datoUtils.datoErInnenforPeriode(dagensDato, periodeStartFoer, periodeASlutt)).isTrue();

    }
}
