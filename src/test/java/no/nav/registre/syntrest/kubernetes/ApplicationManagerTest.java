package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import net.jodah.concurrentunit.Waiter;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("ApplicationManagerTest")
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = ApplicationManagerTestConfig.class)
@EnableAutoConfiguration
public class ApplicationManagerTest {

    @Autowired
    private KubernetesController kubernetesController;
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private RestTemplate restTemplate;

    ApplicationManager globalManager;

    @Value("${synth-package-unused-uptime}")
    private long SHUTDOWN_TIME_DELAY_SECONDS;

    private SyntConsumer syntConsumerFrikort;
    private SyntConsumer syntConsumerMeldekort;

    @Before
    public void setUp() {
        globalManager = new ApplicationManager(kubernetesController, scheduledExecutorService);
        syntConsumerFrikort = new SyntConsumer(globalManager, "synthdata-frikort");
        // Meldekort is reserved for only being deployed ONCE
        syntConsumerMeldekort = new SyntConsumer(globalManager, "synthdata-meldekort");
    }


    @Test
    public void isAlive() {
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(true);

        boolean res = globalManager.applicationIsAlive("MYAPP");
        assertTrue(res);
        Mockito.verify(kubernetesController).isAlive("MYAPP");
    }


    @Test
    public void startApplicationWhileDeployed() throws ApiException {
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(true);

        try {
            manager.startApplication(syntConsumerFrikort);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().containsKey(syntConsumerFrikort.getAppName()));
        Mockito.verify(kubernetesController).takedownImage("synthdata-frikort");
    }


    @Test
    public void startApplicationFailed() throws InterruptedException, ApiException {
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(false);
        Mockito.doThrow(ApiException.class).when(kubernetesController).deployImage(Mockito.anyString());
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);

        try {
            manager.startApplication(syntConsumerFrikort);
        } catch (ApiException | InterruptedException e) { assertTrue(true); }
        assertEquals(0, manager.getActiveApplications().size());
    }


    // RACE CONDITION IN THIS THREAD...
    @Test
    public void startSameApplicationAtSameTime() throws InterruptedException, ApiException, TimeoutException {
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);
        Mockito.when(kubernetesController.isAlive(Mockito.anyString()))
                .thenReturn(false)
                .thenReturn(true);

        Waiter waiter = new Waiter();
        new Thread(() -> {
            try {
                manager.startApplication(syntConsumerMeldekort);
            } catch (InterruptedException | ApiException e) {
                fail();
            }
            waiter.resume();
        }).start();
        new Thread(() -> {
            try {
                manager.startApplication(syntConsumerMeldekort);
            } catch (InterruptedException | ApiException e) {
                fail();
            }
            waiter.resume();
        }).start();
        waiter.await(1, TimeUnit.SECONDS, 2);

        assertEquals(1, manager.getActiveApplications().size());
        Mockito.verify(kubernetesController, Mockito.times(1)).deployImage("synthdata-meldekort");
        Mockito.verify(kubernetesController, Mockito.times(2)).takedownImage("synthdata-meldekort");
    }


    // Possible race condition?
    @Test
    public void startAndShutdownApplication() throws InterruptedException, ApiException {
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);
        SyntConsumer syntConsumerInntekt = new SyntConsumer(manager, "synthdata-inntekt");
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(true);

        manager.startApplication(syntConsumerInntekt);
        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().keySet().contains("synthdata-inntekt"));
        Thread.sleep((SHUTDOWN_TIME_DELAY_SECONDS * 1000) + 100);
        Mockito.verify(kubernetesController).takedownImage(eq("synthdata-inntekt"));
        assertEquals(0, manager.getActiveApplications().size());
    }
}
