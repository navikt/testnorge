package no.nav.registre.syntrest.consumer;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SyntConsumerTest {

    /*
     * Test for å sjekke at data blir returnert *før* applikasjonen blir tatt ned.
     */

    /*
     * Test for å sjekke concurrency, at bare én har tilgang på å syntetisere data av gangen.
     */

    /*
     * Test for å sjekke at applikasjonen ikke tas ned hvis det er flere som venter på å bruke den.
     */

    /*
     * Test for å sjekke at applikasjonen ikke tar ned applikasjonen, når en ny consumer har koblet seg på
     * etter at den forrige egentlig er ferdig med å syntetisere (5-min shutdown delay).
     */
}
