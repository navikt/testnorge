package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
class TelefonnummerServiceTest {

    private static final String IDENT = "12445612301";

    @InjectMocks
    private TelefonnummerService telefonnummerService;

    @Test
    void whenTelefonnummerIsAbsent_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(telefonnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: nummer er påkrevet felt")));
    }

    @Test
    void whenTelefonnummerContainsNonDigitCharacters_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("ABC123")
                .isNew(true)
                .build();

        StepVerifier.create(telefonnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: nummer kan kun inneholde tallsifre")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"23", "12345678901234567"})
    void whenTelefonnummerContainsTooFewDigits_thenThrowException(String input) {

        var request = TelefonnummerDTO.builder()
                .nummer(input)
                .isNew(true)
                .build();

        StepVerifier.create(telefonnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre")));
    }

    @Test
    void whenLandskodeIsAbsent_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("1213123")
                .isNew(true)
                .build();

        StepVerifier.create(telefonnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: landskode er påkrevet felt")));
    }

    @Test
    void whenLandskodeInvalidFormat_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("123123")
                .landskode("-6332")
                .isNew(true)
                .build();

        StepVerifier.create(telefonnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: Landkode består av ledende + " +
                                                                          "(plusstegn) fulgt av  1 til 5 sifre")));
    }

    @Test
    void whenPriorityIsMissing_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("243442")
                .landskode("+323")
                .isNew(true)
                .build();

        StepVerifier.create(telefonnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: prioritet er påkrevet")));
    }

    @Test
    void whenPriorityOtherThan1or2_thenThrowException() {

        var request = TelefonnummerDTO.builder()
                .nummer("243442")
                .landskode("+323")
                .prioritet(3)
                .isNew(true)
                .build();

        StepVerifier.create(telefonnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummerets prioritet må være 1 eller 2")));
    }

    @Test
    void whenPriority2ExistsBefore1_thenThrowException() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                .ident(IDENT)
                .telefonnummer(
                        List.of(TelefonnummerDTO.builder()
                                .nummer("243442")
                                .landskode("+323")
                                .prioritet(2)
                                .isNew(true)
                                .build()))
                .build())
                .build();

        StepVerifier.create(telefonnummerService.convert(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: prioritet 1 må angis før 2 kan benyttes")));
    }

    @Test
    void whenGivenPriorityExistsMultipleTimes_thenThrowException() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                .ident(IDENT)
                .telefonnummer(
                        List.of(TelefonnummerDTO.builder()
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
                                        .build()))
                .build())
                .build();

        StepVerifier.create(telefonnummerService.convert(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Telefonnummer: prioritet 1 og prioritet 2 kan kun benyttes én gang hver")));
    }
}