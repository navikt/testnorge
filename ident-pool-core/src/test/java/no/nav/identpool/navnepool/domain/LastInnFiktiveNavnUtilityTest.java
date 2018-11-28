package no.nav.identpool.navnepool.domain;

import static no.nav.identpool.navnepool.domain.LastInnFiktiveNavnUtility.loadListFromCsvFile;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class LastInnFiktiveNavnUtilityTest {

    /**
     * Tester at FiktiveNavnConfig#loadListFromCsvFile laster inn alle navn i csv-fil
     * og returnerer dem i liste. Navnene skal være på blokkbokstav (fordi TPS krever
     * blokkbokstaver i konsumentenes skdmeldingene).
     *
     * @throws IOException
     */
    @Test
    public void shouldloadListFromCsvFile() throws IOException {
        List strings = loadListFromCsvFile("__files/navnepool/test.csv");
        assertTrue(strings.contains("AKTIV"));
        assertTrue(strings.contains("AKTPÅGIVENDE"));
    }
}