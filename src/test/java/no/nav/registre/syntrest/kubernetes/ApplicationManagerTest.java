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
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.ArgumentMatchers.anyString;
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
        syntConsumerFrikort = new SyntConsumer(globalManager, restTemplate, SyntAppNames.FRIKORT);
        // Meldekort is reserved for only being deployed ONCE
        syntConsumerMeldekort = new SyntConsumer(globalManager, restTemplate, SyntAppNames.MELDEKORT);
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

        int res  = manager.startApplication(syntConsumerFrikort);
        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().containsKey(syntConsumerFrikort.getAppName()));
        assertEquals(0, res);
        Mockito.verify(kubernetesController).takedownImage(SyntAppNames.FRIKORT.getName());
    }


    @Test
    public void startApplicationFailed() throws InterruptedException, ApiException {
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(false);
        Mockito.doThrow(ApiException.class).when(kubernetesController).deployImage(Mockito.anyString());
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);

        int res = manager.startApplication(syntConsumerFrikort);
        assertEquals(0, manager.getActiveApplications().size());
        assertEquals(-1, res);
        Mockito.verify(kubernetesController).takedownImage(SyntAppNames.FRIKORT.getName());
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
            manager.startApplication(syntConsumerMeldekort);
            waiter.resume();
        }).start();
        new Thread(() -> {
            manager.startApplication(syntConsumerMeldekort);
            waiter.resume();
        }).start();
        waiter.await(1, TimeUnit.SECONDS, 2);

        assertEquals(1, manager.getActiveApplications().size());
        Mockito.verify(kubernetesController, Mockito.times(1)).deployImage(SyntAppNames.MELDEKORT.getName());
        Mockito.verify(kubernetesController, Mockito.times(2)).takedownImage(SyntAppNames.MELDEKORT.getName());
    }


    // Possible race condition?
    @Test
    public void startAndShutdownApplication() throws InterruptedException, ApiException {
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);
        SyntConsumer syntConsumerInntekt = new SyntConsumer(manager, restTemplate, SyntAppNames.INNTEKT);
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(true);

        manager.startApplication(syntConsumerInntekt);
        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().keySet().contains(SyntAppNames.INNTEKT.getName()));
        Thread.sleep((SHUTDOWN_TIME_DELAY_SECONDS * 1000) + 100);
        Mockito.verify(kubernetesController).takedownImage(eq(SyntAppNames.INNTEKT.getName()));
        assertEquals(0, manager.getActiveApplications().size());
    }
}
