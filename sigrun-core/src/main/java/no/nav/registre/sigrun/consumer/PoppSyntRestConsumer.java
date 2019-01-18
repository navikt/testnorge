package no.nav.registre.sigrun.consumer;

import no.nav.registre.sigrun.globals.RestConnections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class PoppSyntRestConsumer {

    @Value("${sigrunstub-url}")
    private String sigrunUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestConnections restConnections;

    @Async
    public CompletableFuture<List<Map<String, Object>>> getPoppMeldingerFromSyntRest(String[] fnrs) {
        List<Map<String, Object>> result = restTemplate.postForObject(restConnections.CONNECTION_POPP, fnrs, List.class);
        //System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }

    public boolean sendDataToSigrunstub(List<Map<String, Object>> meldinger){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(meldinger, headers);
        ResponseEntity response = restTemplate.postForEntity(sigrunUrl, entity, List.class);
        return true;
    }


}
