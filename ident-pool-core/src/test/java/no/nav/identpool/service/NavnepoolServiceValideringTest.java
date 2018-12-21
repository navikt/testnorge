package no.nav.identpool.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import no.nav.identpool.domain.Navn;
import org.springframework.test.util.ReflectionTestUtils;

class NavnepoolServiceValideringTest {

    private List<String> validFornavn = Arrays.asList("fornavn1", "fornavn2");
    private List<String> validEtternavn = Arrays.asList( "etternavn1", "etternavn2", "etternavn3");

    private NavnepoolService navnepoolService = new NavnepoolService();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(navnepoolService, "validFornavn", validFornavn);
        ReflectionTestUtils.setField(navnepoolService, "validEtternavn", validEtternavn);
    }

    @DisplayName("Metoden isValid skal returnere true kun DERSOM minst en av fornavn og etternavn er oppgitt, " +
            "og deres verdier er gyldige (dvs. er å finne i listen med godkjente fornavn og etternavn)")
    @ParameterizedTest
    @CsvSource({
            "fornavn1, etternavn1, true",
            ", etternavn1, true", // null value
            "fornavn1, , true",
            ", , false",
            "tullenavn, tullenavn, false"
    })
    void testValidering(String fornavn, String etternavn, boolean expectedValid) {
        Boolean actualValid = navnepoolService.isValid(new Navn(fornavn, etternavn));
        assertEquals(expectedValid, actualValid);
    }
}