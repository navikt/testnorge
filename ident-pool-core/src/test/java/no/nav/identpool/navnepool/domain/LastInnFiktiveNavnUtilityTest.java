package no.nav.identpool.navnepool.domain;

import static no.nav.identpool.navnepool.domain.LastInnFiktiveNavnUtility.loadListFromCsvFile;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class LastInnFiktiveNavnUtilityTest {

    @Test
    @DisplayName("Tester at FiktiveNavnConfig#loadListFromCsvFile laster inn alle navn i csv-fil\n" +
            " og returnerer dem i liste. Navnene skal være på blokkbokstav (fordi TPS krever\n" +
            " blokkbokstaver i konsumentenes skdmeldingene)")
    void shouldloadListFromCsvFile() throws IOException {
        List strings = loadListFromCsvFile("__files/navnepool/test.csv");
        assertTrue(strings.contains("AKTIV"));
        assertTrue(strings.contains("AKTPÅGIVENDE"));
    }
}