package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@ActiveProfiles("ConsumerTest")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConsumerTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableAutoConfiguration
public class SyntConsumerTest {

    @MockBean
    private ApplicationManager applicationManager;
    @MockBean
    private RestTemplate restTemplate;

    private static final String numberAndCodeUrl = "generateNumberAndConde/{code}/{number}";

    @InjectMocks
    private SyntConsumer testConsumer;

    @Before
    public void setup() {
        testConsumer = new SyntConsumer(applicationManager, "test-consumer");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void applicationNotAliveStartedOk() {
        Mockito.when(applicationManager.applicationIsAlive(Mockito.anyString())).thenReturn(false);
        Mockito.when(restTemplate.exchange(RequestEntity.get(new UriTemplate(numberAndCodeUrl).expand(2, "dummyCode")).build(), Object.class)).thenReturn(ResponseEntity.ok().body("OK"));

        String result = (String) testConsumer.synthesizeData(UriExpander.createRequestEntity(numberAndCodeUrl,"dummyCode", 2), Object.class);

        try {
            Mockito.verify(applicationManager, Mockito.atLeastOnce()).startApplication(Mockito.any(SyntConsumer.class));
        } catch (ApiException | InterruptedException e) { fail(); }
        assertEquals("OK", result);
    }

    @Test
    public void applicationAliveReturnData() {
        Mockito.when(restTemplate.exchange(RequestEntity.post(new UriTemplate("dummyUrl").expand()).body("Somebody To Love"), Object.class)).thenReturn(ResponseEntity.ok().body("OK"));

        String result = (String) testConsumer.synthesizeData(UriExpander.createRequestEntity("dummyUrl","Somebody To Love"), Object.class);

        try {
            Mockito.verify(applicationManager).startApplication(testConsumer);
        } catch (ApiException | InterruptedException e) { fail();}
        assertEquals("OK", result);
    }

    @Test
    public void shutdownApplicationTest() {

        testConsumer.shutdownApplication();

        Mockito.verify(applicationManager).shutdownApplication(testConsumer.getAppName());
    }
}
