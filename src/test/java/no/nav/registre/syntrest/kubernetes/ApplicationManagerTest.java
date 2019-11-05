package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import net.jodah.concurrentunit.Waiter;
import no.nav.registre.syntrest.config.AppConfig;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.utils.SyntAppNames;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {ApplicationManager.class, AppConfig.class})
@EnableAutoConfiguration
public class ApplicationManagerTest {

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private KubernetesController kubernetesController;

    @Autowired
    private ApplicationManager applicationManager;

    private SyntConsumer syntConsumerInntekt;
    private SyntConsumer syntConsumerFrikort;

    @Before
    public void setUp() {
        syntConsumerInntekt = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.INST);
        syntConsumerFrikort = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.FRIKORT);
    }


    /*
     * En applikasjon bør ikke kunne få en takedown request samtidig som den venter på å bli
     * deployet.
     */
    @Test
    public void createAndShutdown() throws InterruptedException, ApiException, TimeoutException {
        Mockito.doNothing().when(kubernetesController).deployImage("synthdata-frikort");
        Mockito.doNothing().when(kubernetesController).takedownImage("synthdata-frikort");

        applicationManager.startApplication(syntConsumerFrikort);

        Waiter waiter = new Waiter();
        new Thread(() -> {
            applicationManager.startApplication(syntConsumerFrikort);
            waiter.assertEquals(1, applicationManager.getActiveApplications().size());
            waiter.resume();
        }).start();
        new Thread(() -> {
            applicationManager.shutdownApplication(syntConsumerFrikort.getAppName());
            waiter.assertEquals(0, applicationManager.getActiveApplications().size());
            waiter.resume();
        }).start();
        waiter.await(1, TimeUnit.SECONDS, 2);

        // In case the shutdown runs before start..
        applicationManager.shutdownApplication(syntConsumerFrikort.getAppName());
        assertEquals(0, applicationManager.getActiveApplications().size());
    }

    /*
     * To forskjellige applikasjoner bør kunne startes samtidig. Samme applikasjon bør ikke kunne
     * startes to ganger mens den holder på å deployes.
     */
    @Test
    public void concurrentStart() throws TimeoutException, InterruptedException, ApiException {
        Mockito.doNothing().when(kubernetesController).deployImage("synthdata-frikort");
        Mockito.doNothing().when(kubernetesController).takedownImage("synthdata-frikort");

        Waiter waiter = new Waiter();
        new Thread(() -> {
            applicationManager.startApplication(syntConsumerFrikort);
            waiter.resume();
        }).start();
        new Thread(() -> {
            applicationManager.startApplication(syntConsumerFrikort);
            waiter.resume();
        }).start();
        new Thread(() -> {
            applicationManager.startApplication(syntConsumerInntekt);
            waiter.resume();
        }).start();
        waiter.await(1, TimeUnit.SECONDS, 3);
        assertEquals(2, applicationManager.getActiveApplications().size());
        assertTrue(applicationManager.getActiveApplications().containsKey(syntConsumerFrikort.getAppName()));
        assertTrue(applicationManager.getActiveApplications().containsKey(syntConsumerInntekt.getAppName()));

        applicationManager.shutdownApplication(syntConsumerFrikort.getAppName());
        applicationManager.shutdownApplication(syntConsumerInntekt.getAppName());
        assertEquals(0, applicationManager.getActiveApplications().size());
    }

    /*
     * Hvis to forespørringer til samme, kjørende applikasjon skjer samtidig skal de returnere sekvensielt.
     */
    @Test
    public void concurrentPackageUpdate() throws TimeoutException, InterruptedException, ApiException {
        Mockito.doNothing().when(kubernetesController).deployImage("synthdata-frikort");
        Mockito.doNothing().when(kubernetesController).takedownImage("synthdata-frikort");

        Waiter waiter = new Waiter();
        new Thread(() -> {
            applicationManager.updateAccessedPackages(syntConsumerFrikort);
            waiter.assertTrue(applicationManager.getActiveApplications().containsKey("synthdata-frikort"));
            waiter.assertEquals(applicationManager.getActiveApplications().size(), 1);
            waiter.resume();
        }).start();

        new Thread(() -> {
            applicationManager.updateAccessedPackages(syntConsumerFrikort);
            waiter.assertTrue(applicationManager.getActiveApplications().containsKey("synthdata-frikort"));
            waiter.assertEquals(applicationManager.getActiveApplications().size(), 1);
            waiter.resume();
        }).start();
        waiter.await(1, TimeUnit.SECONDS, 2);

        applicationManager.shutdownApplication("synthdata-frikort");
        assertEquals(0, applicationManager.getActiveApplications().size());
    }

    /*
     * Code coverage for isAlive
     */
    @Test
    public void isAliveTest() {
        Mockito.doReturn(true).when(kubernetesController).isAlive(anyString());
        assertTrue(applicationManager.applicationIsAlive("ANY_APP"));
    }
}
