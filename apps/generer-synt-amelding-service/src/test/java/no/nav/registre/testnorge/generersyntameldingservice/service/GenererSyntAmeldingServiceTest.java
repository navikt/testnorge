package no.nav.registre.testnorge.generersyntameldingservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class GenererSyntAmeldingServiceTest {

    @InjectMocks
    private GenererSyntAmeldingService service;

    @Test
    void shouldGetCorrectAntallMeldinger() {
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 7))).isEqualTo(1);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1))).isEqualTo(13);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 10, 15))).isEqualTo(10);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 7, 23))).isEqualTo(19);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 3), LocalDate.of(2020, 3, 1))).isEqualTo(3);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1))).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWithIncorrectDates() {
        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> service.getAntallMeldinger(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 2, 1))
        );
    }
}
