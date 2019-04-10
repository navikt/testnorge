package no.nav.registre.sdForvalter.consumer.rs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@Profile("test")
public class TpConsumerTest {

    private final String environment = "t1";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TpConsumer tpConsumer;

    @Test
    public void sendFnrsToTpSuccess() {

        Set<String> data = new HashSet<>();
        data.add("123");

        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(new ResponseEntity<>(data, HttpStatus.OK));

        boolean didSend = tpConsumer.send(data, environment);
        assertTrue(didSend);
        verify(restTemplate, times(1)).exchange(any(), (Class<Object>) any());
    }

    @Test
    public void sendFnrsToTpFail() {

        Set<String> data = new HashSet<>();
        data.add("123");

        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(new ResponseEntity<>(Collections.emptySet(), HttpStatus.CONFLICT));

        boolean didSend = tpConsumer.send(data, environment);
        assertFalse(didSend);
        verify(restTemplate, times(1)).exchange(any(), (Class<Object>) any());
    }

    @Test
    public void findFnrsFoundOne() {
        Set<String> data = new HashSet<>();
        data.add("123");
        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(new ResponseEntity<>(data, HttpStatus.OK));

        Set<String> fnrs = tpConsumer.findFnrs(environment);
        assertFalse(fnrs.isEmpty());
    }

    @Test
    public void findFnrsFoundNone() {
        when(restTemplate.getForObject(any(), any())).thenReturn(Collections.emptySet());

        Set<String> fnrs = tpConsumer.findFnrs(environment);
        assertTrue(fnrs.isEmpty());
    }
}