package no.nav.identpool.navnepool.domain;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class FiktiveNavnConfigTest {

    @Test
    public void shouldloadListFromCsvFile() throws IOException {
        FiktiveNavnConfig c = new FiktiveNavnConfig();
        List strings = c.loadListFromCsvFile("__files/navnepool/test.csv");
        assertTrue(strings.contains("aktiv"));
        assertTrue(strings.contains("aktpågivende"));
    }
}