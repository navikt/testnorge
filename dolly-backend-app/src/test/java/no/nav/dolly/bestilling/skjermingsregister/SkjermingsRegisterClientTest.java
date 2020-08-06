package no.nav.dolly.bestilling.skjermingsregister;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SkjermingsRegisterClientTest {

    @InjectMocks
    private SkjermingsRegisterClient skjermingsRegisterClient;

    @Test
    public void should_returnere_true_aktiv_egenansatt_for_aktiv_ansatt() {

        boolean result = skjermingsRegisterClient.isAktivEgenansatt(now().minusDays(1), null);
        assertTrue(result);
    }

    @Test
    public void should_returnere_false_aktiv_egenansatt_for_ikke_ansatt() {

        boolean result = skjermingsRegisterClient.isAktivEgenansatt(null, null);
        assertFalse(result);
    }

    @Test
    public void should_returnere_false_aktiv_egenansatt_for_tidligere_ansatt() {

        boolean result = skjermingsRegisterClient.isAktivEgenansatt(now().minusMonths(5), now().minusDays(1));
        assertFalse(result);
    }

    @Test
    public void should_returnere_false_opphoert_ansatt_for_aktiv_ansatt() {

        boolean result = skjermingsRegisterClient.isOpphoertEgenAnsatt(now().minusMonths(5), null);
        assertFalse(result);
    }

    @Test
    public void should_returnere_false_opphoert_ansatt_for_ikke_ansatt() {

        boolean result = skjermingsRegisterClient.isOpphoertEgenAnsatt(null, null);
        assertFalse(result);
    }

    @Test
    public void should_returnere_true_opphoert_ansatt_for_tidligere_ansatt() {

        boolean result = skjermingsRegisterClient.isOpphoertEgenAnsatt(now().minusMonths(2), now().minusDays(1));
        assertTrue(result);
    }
}
