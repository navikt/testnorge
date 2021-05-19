package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.PdlTelefonnummer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .isNew(true).build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer er påkrevd felt"));
    }

    @Test
    void whenTelefonnummerContainsNonDigitCharacters_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("ABC123")
                        .isNew(true).build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan kun inneholde tallsifre"));
    }

    @Test
    void whenTelefonnummerContainsTooFewDigits_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("23")
                        .isNew(true).build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre"));
    }

    @Test
    void whenTelefonnummerContainsTooManyDigits_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("12345678901234567")
                        .isNew(true).build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre"));
    }

    @Test
    void whenLandskodeIsAbsent_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("1213123")
                        .isNew(true).build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: landskode er påkrevd felt"));
    }

    @Test
    void whenLandskodeInvalidFormat_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("123123")
                        .landskode("-6332")
                        .isNew(true).build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: Landkode består av ledende + " +
                "(plusstegn) fulgt av  1 til 5 sifre"));
    }

    @Test
    void whenPriorityIsMissing_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("243442")
                        .landskode("+323")
                        .isNew(true).build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet er påkrevd"));
    }

    @Test
    void whenPriorityOtherThan1or2_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("243442")
                        .landskode("+323")
                        .prioritet(3)
                        .isNew(true)
                        .build())));

        assertThat(exception.getMessage(), containsString("Telefonnummerets prioritet må være 1 eller 2"));
    }

    @Test
    void whenPriority2ExistsBefore1_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                        .nummer("243442")
                        .landskode("+323")
                        .prioritet(2)
                        .isNew(true)
                        .build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet 1 må angis før 2 kan benyttes"));
    }

    @Test
    void whenGivenPriorityExistsMultipleTimes_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                telefonnummerService.convert(List.of(PdlTelefonnummer.builder()
                                .nummer("243442")
                                .landskode("+323")
                                .prioritet(1)
                                .isNew(true)
                                .build(),
                        PdlTelefonnummer.builder()
                                .nummer("2432343242442")
                                .landskode("+1")
                                .prioritet(1)
                                .isNew(true)
                                .build())));

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet 1 og prioritet 2 kan kun benyttes 1 gang hver"));
    }
}