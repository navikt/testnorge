package no.nav.registre.sdForvalter.consumer.rs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import no.nav.registre.sdForvalter.consumer.rs.request.KrrRequest;
import no.nav.registre.sdForvalter.database.model.KrrModel;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "src/test/resources/application-test.properties")
@ContextConfiguration(classes = {KrrConsumer.class, RestTemplate.class})
public class KrrConsumerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KrrConsumer krrConsumer;

    @Test
    public void sendFikkLagret() {
        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        Set<KrrModel> data = new HashSet<>();
        data.add(KrrModel.builder()
                .fnr("123")
                .email("abc@123.com")
                .emailValid(true)
                .reserved(false)
                .sdp(false)
                .sms("123456079")
                .smsValid(true)
                .build());
        Set<String> saved = krrConsumer.send(data);
        verify(restTemplate, times(1)).exchange(any(), (Class<Object>) any());
        assertEquals(1, saved.size());
    }

    @Test
    public void sendFikkIkkeLagret() {
        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        Set<KrrModel> data = new HashSet<>();
        data.add(KrrModel.builder()
                .fnr("123")
                .email("abc@123.com")
                .emailValid(true)
                .reserved(false)
                .sdp(false)
                .sms("123456079")
                .smsValid(true)
                .build());
        Set<String> saved = krrConsumer.send(data);
        verify(restTemplate, times(1)).exchange(any(), (Class<Object>) any());
        assertEquals(0, saved.size());
    }

    @Test
    public void getContactInformationFantIdent() {
        Set<String> data = new HashSet<>();
        data.add("123");
        List<KrrRequest> outputData = new ArrayList<>();
        outputData.add(KrrRequest.builder().fnr("123").build());
        when(restTemplate.exchange(any(), (ParameterizedTypeReference<Object>) any())).thenReturn(new ResponseEntity<>(outputData, HttpStatus.OK));
        Set<String> saved = krrConsumer.getContactInformation(data);
        verify(restTemplate, times(1)).exchange(any(), (ParameterizedTypeReference<Object>) any());
        assertEquals(1, saved.size());
        assertTrue(saved.contains("123"));
    }

    @Test
    public void getContactInformationFantIkkeIdent() {
        Set<String> data = new HashSet<>();
        data.add("123");
        List<KrrRequest> outputData = new ArrayList<>();
        when(restTemplate.exchange(any(), (ParameterizedTypeReference<Object>) any())).thenReturn(new ResponseEntity<>(outputData, HttpStatus.OK));
        Set<String> saved = krrConsumer.getContactInformation(data);
        verify(restTemplate, times(1)).exchange(any(), (ParameterizedTypeReference<Object>) any());
        assertEquals(0, saved.size());
    }
}