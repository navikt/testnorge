package no.nav.dolly.syntdata;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class SyntdataConsumer {

    private static final String NUM_TO_GENERATE = "?numToGenerate=";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public ResponseEntity generate(String path, Integer numToGenerateValue) {
        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getSyntdata().getUrl() + path + NUM_TO_GENERATE + numToGenerateValue))
                        .build(), JsonNode.class);
    }
}