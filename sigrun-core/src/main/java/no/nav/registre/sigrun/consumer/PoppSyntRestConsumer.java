package no.nav.registre.sigrun.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class PoppSyntRestConsumer {

    @Value("${sigrunstub-url}")
    private String sigrunUrl;

    @Value("${synth-popp-url}")
    private String synthPoppUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Async
    public CompletableFuture<List<Map<String, Object>>> getPoppMeldingerFromSyntRest(String[] fnrs) {
        List<Map<String, Object>> result = restTemplate.postForObject(synthPoppUrl, fnrs, List.class);
        return CompletableFuture.completedFuture(result);
    }

    public boolean sendDataToSigrunstub(List<Map<String, Object>> meldinger) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(meldinger, headers);
        ResponseEntity response = restTemplate.postForEntity(sigrunUrl, entity, List.class);
        return true;
    }

}
