package no.nav.registre.frikort.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Dictionary;
import java.util.HashMap;

@Component
@Slf4j
public class FrikortSyntetisererenConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${synthdata.frikort.url}")
    private String syntServerUrl;

    public HashMap<String, SyntFrikortResponse[]> hentFrikortFromSyntRest(HashMap<String, Integer> request) {

        var postRequest = RequestEntity.post(URI.create(syntServerUrl + "/api/v1/generate")).body(request);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<HashMap<String, SyntFrikortResponse[]>>(){}).getBody();

    }
}