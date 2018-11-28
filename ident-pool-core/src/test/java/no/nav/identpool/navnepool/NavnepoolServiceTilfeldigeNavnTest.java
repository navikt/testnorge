package no.nav.identpool.navnepool;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.identpool.navnepool.domain.Navn;

@RunWith(MockitoJUnitRunner.class)
public class NavnepoolServiceTilfeldigeNavnTest {

    private List<String> validFornavn = Arrays.asList("fornavn1", "fornavn2");
    private List<String> validEtternavn = Arrays.asList("etternavn1", "etternavn2", "etternavn3");

    private NavnepoolService navnepoolService;

    @Test
    public void shouldGetAListOfRandomNames() {
        navnepoolService = new NavnepoolService(validFornavn, validEtternavn);

        int antallNavn = 2;
        List<Navn> tilfeldigeNavn = navnepoolService.hentTilfeldigeNavn(antallNavn);

        assertEquals(antallNavn, tilfeldigeNavn.size());
        List<String> tilfeldigeFornavn = tilfeldigeNavn.stream().map(Navn::getFornavn).collect(Collectors.toList());
        List<String> tilfeldigeEtternavn = tilfeldigeNavn.stream().map(Navn::getEtternavn).collect(Collectors.toList());
        assertTrue(validFornavn.containsAll(tilfeldigeFornavn));
        assertTrue(validEtternavn.containsAll(tilfeldigeEtternavn));
    }
}