package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlTelefonnummer;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TelefonnummerCommandTest {

    @Test
    void whenTelefonnummerIsAbsent_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .isNew(true).build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer er påkrevd felt"));
    }

    @Test
    void whenTelefonnummerContainsNonDigitCharacters_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("ABC123")
                        .isNew(true).build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan kun inneholde tallsifre"));
    }

    @Test
    void whenTelefonnummerContainsTooFewDigits_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("23")
                        .isNew(true).build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre"));
    }

    @Test
    void whenTelefonnummerContainsTooManyDigits_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("12345678901234567")
                        .isNew(true).build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre"));
    }

    @Test
    void whenLandskodeIsAbsent_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("1213123")
                        .isNew(true).build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: landskode er påkrevd felt"));
    }

    @Test
    void whenLandskodeInvalidFormat_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("123123")
                        .landskode("-6332")
                        .isNew(true).build())).call());

        assertThat(exception.getMessage(), containsString("Landkode har format 1 til 4 sifre, eventuelt med ledende plusstegn (+)"));
    }

    @Test
    void whenPriorityIsMissing_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("243442")
                        .landskode("+323")
                        .isNew(true).build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet er påkrevd"));
    }

    @Test
    void whenPriorityOtherThan1or2_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("243442")
                        .landskode("+323")
                        .prioritet(3)
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummerets prioritet må være 1 eller 2"));
    }

    @Test
    void whenPriority2ExistsBefore1_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
                        .nummer("243442")
                        .landskode("+323")
                        .prioritet(2)
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet 1 må angis før 2 kan benyttes"));
    }

    @Test
    void whenGivenPriorityExistsMultipleTimes_thenThrowException() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new TelefonnummerCommand(List.of(PdlTelefonnummer.builder()
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
                                .build())).call());

        assertThat(exception.getMessage(), containsString("Telefonnummer: prioritet 1 og prioritet 2 kan kun benyttes 1 gang hver"));
    }
}