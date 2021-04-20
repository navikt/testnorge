package no.nav.registre.testnorge.generersyntameldingservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class GenererSyntAmeldingServiceTest {

    @InjectMocks
    private GenererSyntAmeldingService service;

    @Test
    public void shouldGetCorrectAntallMeldinger() {
        assertThat(service.getAntallEndringer(LocalDate.now(), LocalDate.now().plusDays(7))).isEqualTo(1);
        assertThat(service.getAntallEndringer(LocalDate.now(), LocalDate.now().minusDays(7))).isZero();
        assertThat(service.getAntallEndringer(LocalDate.now(), LocalDate.now().plusYears(1))).isEqualTo(13);
        assertThat(service.getAntallEndringer(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 10, 15))).isEqualTo(10);
        assertThat(service.getAntallEndringer(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 7, 23))).isEqualTo(19);
    }
}
