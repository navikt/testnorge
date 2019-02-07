package no.nav.registre.sigrun.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class SigrunStubConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sigrunstub.url}")
    private String sigrunUrl;

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public ResponseEntity sendDataToSigrunstub(List<Map<String, Object>> meldinger, String testdataEier) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("testdataEier", testdataEier);
        HttpEntity entity = new HttpEntity(meldinger, headers);
        return restTemplate.postForEntity(sigrunUrl, entity, List.class);
    }
}
