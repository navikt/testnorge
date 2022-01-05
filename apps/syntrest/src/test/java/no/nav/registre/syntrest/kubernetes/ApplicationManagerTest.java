package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.consumer.SyntGetConsumer;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ApplicationManagerTest {

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Value("${synth-package-unused-uptime}")
    private long SHUTDOWN_TIME_DELAY_SECONDS;

    private ApplicationManager globalManager;

    @Autowired
    private SyntGetConsumer<List<String>> navConsumer;

    @MockBean
    private KubernetesController kubernetesController;

    @Before
    public void setUp() {
        globalManager = new ApplicationManager(kubernetesController, scheduledExecutorService);
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
            manager.startApplication(navConsumer);
        } catch (InterruptedException e) {
            fail();
        }
        manager.scheduleShutdown(navConsumer, SHUTDOWN_TIME_DELAY_SECONDS);
        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().containsKey(navConsumer.getAppName()));
    }

    @Test
    public void startApplicationFailed() throws ApiException {
        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(false);
        Mockito.doThrow(ApiException.class).when(kubernetesController).deployImage(Mockito.anyString());
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);

        try {
            manager.startApplication(navConsumer);
        } catch (ApiException | InterruptedException e) {
            assertTrue(true);
        }
        assertEquals(0, manager.getActiveApplications().size());
    }

    // Possible race condition?
    @Test
    @Ignore
    public void startAndShutdownApplication() throws InterruptedException, ApiException {
        ApplicationManager manager = new ApplicationManager(kubernetesController, scheduledExecutorService);

        Mockito.when(kubernetesController.isAlive(Mockito.anyString())).thenReturn(true);

        manager.startApplication(navConsumer);
        manager.scheduleShutdown(navConsumer, SHUTDOWN_TIME_DELAY_SECONDS);

        assertEquals(1, manager.getActiveApplications().size());
        assertTrue(manager.getActiveApplications().containsKey("synthdata-nav"));

        Thread.sleep((SHUTDOWN_TIME_DELAY_SECONDS * 1000) + 100);
        manager.shutdownApplication("synthdata-nav");

        Mockito.verify(kubernetesController, Mockito.atLeastOnce()).takedownImage("synthdata-nav");
        Thread.sleep((SHUTDOWN_TIME_DELAY_SECONDS * 1000) + 100);
        assertEquals(0, manager.getActiveApplications().size());
    }

}
