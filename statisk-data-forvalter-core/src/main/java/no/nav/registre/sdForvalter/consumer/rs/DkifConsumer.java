package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Slf4j
@Component
public class DkifConsumer implements ConsumerInitializer {

    private final RestTemplate restTemplate;
    private final String dkifUrl;

    public DkifConsumer(RestTemplate restTemplate, @Value("${dkif.rest.api.url}") String dkifUrl) {
        this.restTemplate = restTemplate;
        this.dkifUrl = dkifUrl + "/api/v1";
    }

    @Override
    public void send(Set<Object> data, String environment) {

    }
}
