package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TelefonnummerServiceTest {

    @InjectMocks
    private TelefonnummerService telefonnummerService;

    @Test
    void whenTelefonnummerIsAbsent_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.validate(request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer er påkrevet felt"));
    }

    @Test
    void whenTelefonnummerContainsNonDigitCharacters_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("ABC123")
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.validate(request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan kun inneholde tallsifre"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"23", "12345678901234567"})
    void whenTelefonnummerContainsTooFewDigits_thenThrowException(String input) {

        var request = TelefonnummerDTO.builder()
                .nummer(input)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.validate(request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre"));
    }

    @Test
    void whenLandskodeIsAbsent_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("1213123")
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.validate(request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: landskode er påkrevet felt"));
    }

    @Test
    void whenLandskodeInvalidFormat_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("123123")
                .landskode("-6332")
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.validate(request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: Landkode består av ledende + " +
                "(plusstegn) fulgt av  1 til 5 sifre"));
    }

    @Test
    void whenPriorityIsMissing_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("243442")
                .landskode("+323")
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.validate(request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet er påkrevet"));
    }

    @Test
    void whenPriorityOtherThan1or2_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("243442")
                .landskode("+323")
                .prioritet(3)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.validate(request));

        assertThat(exception.getMessage(), containsString("Telefonnummerets prioritet må være 1 eller 2"));
    }

    @Test
    void whenPriority2ExistsBefore1_thenThrowException() {

        var request = List.of(TelefonnummerDTO.builder()
                .nummer("243442")
                .landskode("+323")
                .prioritet(2)
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert((List<TelefonnummerDTO>) request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet 1 må angis før 2 kan benyttes"));
    }

    @Test
    void whenGivenPriorityExistsMultipleTimes_thenThrowException() {

        var request = List.of(TelefonnummerDTO.builder()
                        .nummer("243442")
                        .landskode("+323")
                        .prioritet(1)
                        .isNew(true)
                        .build(),
                TelefonnummerDTO.builder()
                        .nummer("2432343242442")
                        .landskode("+1")
                        .prioritet(1)
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(request));

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet 1 og prioritet 2 kan kun benyttes én gang hver"));
    }
}