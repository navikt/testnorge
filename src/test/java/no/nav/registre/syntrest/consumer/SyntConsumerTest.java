package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.util.KubeConfig;
import no.nav.registre.syntrest.config.AppConfig;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.utils.SyntAppNames;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class SyntConsumerTest {

    /*
     * Test for å sjekke at data blir returnert *før* applikasjonen blir tatt ned.
     */
    @Test
    public void dataTransferBeforeShutdown() {

    }

    /*
     * Test for å sjekke concurrency, at bare én har tilgang på å syntetisere data av gangen.
     */
    @Test
    public void synthesizationConcurrency(){}

    /*
     * Test for å sjekke at applikasjonen ikke tar ned applikasjonen, når en ny consumer har koblet seg på
     * etter at den forrige egentlig er ferdig med å syntetisere (5-min shutdown delay).
     */
    @Test
    public void lateQueueEntryTakedown() {

    }

    /*
     * SyntConsumerManager test, siden klassen ikke har noe særlig business logikk
     */
    @Test
    public void syntConsumerManagerTest() {
        /*SyntConsumer app = manager.get(SyntAppNames.TP);
        assertEquals("synthdata-tp", app.getAppName());*/
    }
}
