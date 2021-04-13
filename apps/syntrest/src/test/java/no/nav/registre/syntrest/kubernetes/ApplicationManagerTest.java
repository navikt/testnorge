package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("ApplicationManagerTest")
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8082)
@ContextConfiguration(classes = ApplicationManagerTestConfig.class)
@EnableAutoConfiguration
public class ApplicationManagerTest {
/*
    @Autowired
    private KubernetesController kubernetesController;
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Value("${synth-package-unused-uptime}")
    private long SHUTDOWN_TIME_DELAY_SECONDS;


    private ApplicationManager globalManager;

    private SyntConsumer syntConsumerFrikort;

    @Before
    public void setUp() {
        globalManager = new ApplicationManager(kubernetesController, scheduledExecutorService);
        syntConsumerFrikort = new SyntConsumer(globalManager, "synthdata-frikort");
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
        manager.scheduleShutdown(syntConsumerFrikort, SHUTDOWN_TIME_DELAY_SECONDS);
        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().containsKey(syntConsumerFrikort.getAppName()));
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


    // Possible race condition?
    @Test
    public void startAndShutdownApplication() throws InterruptedException, ApiException {
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);
        // SyntConsumer syntConsumerInntekt = new SyntConsumer(manager, "synthdata-inntekt");
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(true);

        manager.startApplication(syntConsumerInntekt);
        manager.scheduleShutdown(syntConsumerInntekt, SHUTDOWN_TIME_DELAY_SECONDS);
        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().keySet().contains("synthdata-inntekt"));
        Thread.sleep((SHUTDOWN_TIME_DELAY_SECONDS * 1000) + 100);
        manager.shutdownApplication("synthdata-inntekt");
        Mockito.verify(kubernetesController, Mockito.atLeastOnce()).takedownImage(eq("synthdata-inntekt"));
        Thread.sleep((SHUTDOWN_TIME_DELAY_SECONDS * 1000) + 100);
        assertEquals(0, manager.getActiveApplications().size());
    }
    */
}
