package no.nav.registre.sdForvalter.consumer.rs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;

@RunWith(SpringJUnit4ClassRunner.class)
@Profile("test")
public class AaregConsumerTest {

    private final String environment = "t1";

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    private AaregConsumer aaregConsumer;

    @Test
    public void sendGyldigeTilAdapter() {

        Set<AaregModel> data = new HashSet<>();
        data.add(new AaregModel("123", 345L));

        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        boolean didSend = aaregConsumer.send(data, environment);
        assertTrue(didSend);
    }

    @Test
    public void sendNoeFeil() {
        Set<AaregModel> data = new HashSet<>();
        data.add(new AaregModel("123", 0));

        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        boolean didSend = aaregConsumer.send(data, environment);
        assertFalse(didSend);
    }
}