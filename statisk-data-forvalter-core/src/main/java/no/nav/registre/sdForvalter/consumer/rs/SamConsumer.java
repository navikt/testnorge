package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Set;

@Slf4j
@Component
public class SamConsumer implements ConsumerInitializer {

    private final RestTemplate restTemplate;
    private final String samUrl;

    public SamConsumer(RestTemplate restTemplate, @Value("${testnorge-sam.rest.api.url}") String samUrl) {
        this.restTemplate = restTemplate;
        this.samUrl = samUrl + "/api/v1";
    }

    public Set<Object> findFnrs(String environment) {
        //TODO: Add sam spesifics
        UriTemplate uriTemplate = new UriTemplate(samUrl + "/");
        return restTemplate.getForObject(uriTemplate.expand(environment), Set.class);
    }


    @Override
    public void send(Set<Object> data, String environment) {

    }
}
