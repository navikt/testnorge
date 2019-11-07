package no.nav.registre.syntrest.consumer;

import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.utils.SyntAppNames;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("ConsumerTest")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConsumerTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableAutoConfiguration
public class SyntConsumerTest {

    @Autowired
    private ApplicationManager applicationManager;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SyntConsumerManager manager;

    private static RequestEntity request;

    @Before
    public void setup() {
        request = new RequestEntity(HttpMethod.GET, new UriTemplate("dummy").expand());
    }

    @Test
    public void callNonexistingApp() {
        SyntConsumer t = manager.get(SyntAppNames.NOAPPLICATION);
        assertTrue(Objects.isNull(t));
    }

    @Test
    public void applicationNotAlive() {
        Mockito.when(applicationManager.applicationIsAlive(Mockito.anyString())).thenReturn(false);
        Mockito.when(applicationManager.startApplication(Mockito.any(SyntConsumer.class))).thenReturn(-1);

        SyntConsumer t = manager.get(SyntAppNames.AAP);
        ResponseEntity result = (ResponseEntity) t.synthesizeData(request);

        assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.verify(applicationManager).startApplication(Mockito.any(SyntConsumer.class));
    }

    @Test
    public void applicationNotAliveStartedOk() {
        Mockito.when(applicationManager.applicationIsAlive(Mockito.anyString())).thenReturn(false);
        Mockito.when(applicationManager.startApplication(Mockito.any(SyntConsumer.class))).thenReturn(0);
        Mockito.when(restTemplate.exchange(request, Object.class)).thenReturn(ResponseEntity.ok().body("OK"));

        SyntConsumer t = manager.get(SyntAppNames.AAP);
        String result = (String) t.synthesizeData(request);

        Mockito.verify(applicationManager, Mockito.atLeastOnce()).startApplication(Mockito.any(SyntConsumer.class));
        assertEquals("OK", result);
    }

    @Test
    public void applicationAliveReturnData() {
        Mockito.when(applicationManager.startApplication(Mockito.any(SyntConsumer.class))).thenReturn(0);
        Mockito.when(restTemplate.exchange(request, Object.class)).thenReturn(ResponseEntity.ok().body("OK"));

        SyntConsumer t = manager.get(SyntAppNames.TPS);
        String result = (String) t.synthesizeData(request);

        Mockito.verify(applicationManager).startApplication(t);
        assertEquals("OK", result);
    }

    @Test
    public void shutdownApplicationTest() {
        SyntConsumer t = manager.get(SyntAppNames.TPS);
        t.shutdownApplication();

        Mockito.verify(applicationManager).shutdownApplication(t.getAppName());
    }
}
