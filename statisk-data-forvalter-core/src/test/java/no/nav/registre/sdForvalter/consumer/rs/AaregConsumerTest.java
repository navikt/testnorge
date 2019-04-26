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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
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

        List<String> okList = new ArrayList<>(1);
        okList.add("123");

        AaregResponse aaregResponse = new AaregResponse(
                okList,
                okList,
                Collections.emptyMap()
        );

        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(ResponseEntity.ok(aaregResponse));

        Map<String, String> statusMap = aaregConsumer.send(data, environment);
        assertTrue(statusMap.isEmpty());
    }

    @Test
    public void sendNoeFeil() {
        Set<AaregModel> data = new HashSet<>();
        data.add(new AaregModel("123", 0));

        Map<String, String> status = new HashMap<>(1);
        status.put("123", "feil");

        AaregResponse aaregResponse = new AaregResponse(
                Collections.emptyList(),
                Collections.emptyList(),
                status
        );

        when(restTemplate.exchange(any(), (Class<Object>) any())).thenReturn(ResponseEntity.ok(aaregResponse));
        Map<String, String> statusMap = aaregConsumer.send(data, environment);
        assertFalse(statusMap.isEmpty());
    }
}