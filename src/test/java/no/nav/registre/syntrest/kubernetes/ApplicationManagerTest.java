package no.nav.registre.syntrest.kubernetes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ApplicationManagerTest {
    // Sjekk om samme applikasjon kan både startes og stoppes samtidig.
    @Test
    public void createAndShutdown() {

    }
    // Sjekk om to forskjellige applikasjoner kan startes samtidig.
    @Test
    public void concurrentStart() {

    }
}
