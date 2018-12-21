package no.nav.identpool.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.identpool.domain.Navn;
import org.springframework.test.util.ReflectionTestUtils;

class NavnepoolServiceTilfeldigeNavnTest {

    private List<String> validFornavn = Arrays.asList("fornavn1", "fornavn2");
    private List<String> validEtternavn = Arrays.asList("etternavn1", "etternavn2", "etternavn3");

    private NavnepoolService navnepoolService = new NavnepoolService();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(navnepoolService, "validFornavn", validFornavn);
        ReflectionTestUtils.setField(navnepoolService, "validEtternavn", validEtternavn);
    }

    @Test
    void shouldGetAListOfRandomNames() {
        int antallNavn = 2;
        List<Navn> tilfeldigeNavn = navnepoolService.hentTilfeldigeNavn(antallNavn);

        assertEquals(antallNavn, tilfeldigeNavn.size());
        List<String> tilfeldigeFornavn = tilfeldigeNavn.stream().map(Navn::getFornavn).collect(Collectors.toList());
        List<String> tilfeldigeEtternavn = tilfeldigeNavn.stream().map(Navn::getEtternavn).collect(Collectors.toList());
        assertTrue(validFornavn.containsAll(tilfeldigeFornavn));
        assertTrue(validEtternavn.containsAll(tilfeldigeEtternavn));
    }
}