package no.nav.dolly.syntdata;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class SyntdataConsumer {

    private static final String NUM_TO_GENERATE = "?numToGenerate=";

    @Autowired
    private ProvidersProps providersProps;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity generate(String path, Integer numToGenerateValue) {
        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getSyntdata().getUrl() + path + NUM_TO_GENERATE + numToGenerateValue))
                        .build(), JsonNode.class);
    }
}