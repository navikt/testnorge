package no.nav.identpool.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class LastInnFiktiveNavnUtilTest {

    @Test
    @DisplayName("Tester at FiktiveNavnService#loadListFromCsvFile laster inn alle navn i csv-fil\n" +
            " og returnerer dem i liste. Navnene skal være på blokkbokstav (fordi TPS krever\n" +
            " blokkbokstaver i konsumentenes SKDmeldinger)")
    void shouldloadListFromCsvFile() throws IOException {
        List strings = LastInnFiktiveNavnUtil.loadListFromCsvFile("__files/navnepool/test.csv");
        assertTrue(strings.contains("AKTIV"));
        assertTrue(strings.contains("AKTPÅGIVENDE"));
    }
}