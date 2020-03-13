package no.nav.dolly.consumer.identpool;

import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentpoolConsumer {

    private static final String FIKTIVE_NAVN_URL = "/api/v1/fiktive-navn/tilfeldig?antall=10";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public ResponseEntity<JsonNode> getPersonnavn() {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getIdentPool().getUrl() + FIKTIVE_NAVN_URL))
                .build(), JsonNode.class);
    }
}
