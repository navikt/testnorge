package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Slf4j
@Component
public class AaregConsumer implements ConsumerInitializer {

    private final RestTemplate restTemplate;
    private final String aaregUrl;

    public AaregConsumer(RestTemplate restTemplate, @Value("${testnorge-aareg.rest.api.url}") String aaregUrl) {
        this.restTemplate = restTemplate;
        this.aaregUrl = aaregUrl + "/api/v1";
    }


    @Override
    public void send(Set<Object> data, String environment) {

    }
}
