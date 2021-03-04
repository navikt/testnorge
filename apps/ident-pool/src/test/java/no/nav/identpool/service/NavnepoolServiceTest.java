package no.nav.identpool.service;

import no.nav.identpool.domain.Navn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NavnepoolServiceTest {

    private final List<String> validFornavn = Arrays.asList("fornavn1", "fornavn2");
    private final List<String> validEtternavn = Arrays.asList( "etternavn1", "etternavn2", "etternavn3");

    private final NavnepoolService navnepoolService = new NavnepoolService();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(navnepoolService, "validFornavn", validFornavn);
        ReflectionTestUtils.setField(navnepoolService, "validEtternavn", validEtternavn);
    }

    @Test
    @DisplayName("Skal hente liste med tilfeldige navn")
    void shouldGetAListOfRandomNames() {
        int antallNavn = 2;
        List<Navn> tilfeldigeNavn = navnepoolService.hentTilfeldigeNavn(antallNavn);

        assertEquals(antallNavn, tilfeldigeNavn.size());
        List<String> tilfeldigeFornavn = tilfeldigeNavn.stream().map(Navn::getFornavn).collect(Collectors.toList());
        List<String> tilfeldigeEtternavn = tilfeldigeNavn.stream().map(Navn::getEtternavn).collect(Collectors.toList());
        assertTrue(validFornavn.containsAll(tilfeldigeFornavn));
        assertTrue(validEtternavn.containsAll(tilfeldigeEtternavn));
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
