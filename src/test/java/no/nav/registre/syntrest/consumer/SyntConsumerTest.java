package no.nav.registre.syntrest.consumer;

import no.nav.registre.syntrest.kubernetes.ApplicationManager;
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

import static org.junit.Assert.assertEquals;

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

    private static RequestEntity request;
    private static final String numberUrl = "generateNumbers/{numToGenerate}";
    private static final String numberAndCodeUrl = "generateNumberAndConde/{code}/{number}";

    private SyntConsumer testConsumer;

    @Before
    public void setup() {
        request = new RequestEntity(HttpMethod.GET, new UriTemplate("dummy").expand());
        testConsumer = new SyntConsumer(applicationManager, restTemplate, "test-consumer");
    }


    @Test
    public void applicationNotAlive() {
        Mockito.when(applicationManager.applicationIsAlive(Mockito.anyString())).thenReturn(false);
        Mockito.when(applicationManager.startApplication(Mockito.any(SyntConsumer.class))).thenReturn(-1);

        ResponseEntity result = (ResponseEntity) testConsumer.generateForNumbers(numberUrl, 2);

        assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.verify(applicationManager).startApplication(Mockito.any(SyntConsumer.class));
    }

    @Test
    public void applicationNotAliveStartedOk() {
        Mockito.when(applicationManager.applicationIsAlive(Mockito.anyString())).thenReturn(false);
        Mockito.when(applicationManager.startApplication(Mockito.any(SyntConsumer.class))).thenReturn(0);
        Mockito.when(restTemplate.exchange(RequestEntity.get(new UriTemplate(numberAndCodeUrl).expand(2, "dummyCode")).build(), Object.class)).thenReturn(ResponseEntity.ok().body("OK"));

        String result = (String) testConsumer.generateForCodeAndNumber(numberAndCodeUrl,"dummyCode", 2);

        Mockito.verify(applicationManager, Mockito.atLeastOnce()).startApplication(Mockito.any(SyntConsumer.class));
        assertEquals("OK", result);
    }

    @Test
    public void applicationAliveReturnData() {
        Mockito.when(applicationManager.startApplication(Mockito.any(SyntConsumer.class))).thenReturn(0);
        Mockito.when(restTemplate.exchange(RequestEntity.post(new UriTemplate("dummyUrl").expand()).body("Somebody To Love"), Object.class)).thenReturn(ResponseEntity.ok().body("OK"));

        String result = (String) testConsumer.synthesizeDataPostRequest("dummyUrl","Somebody To Love");

        Mockito.verify(applicationManager).startApplication(testConsumer);
        assertEquals("OK", result);
    }

    @Test
    public void shutdownApplicationTest() {

        testConsumer.shutdownApplication();

        Mockito.verify(applicationManager).shutdownApplication(testConsumer.getAppName());
    }
}
