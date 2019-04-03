package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;

@Slf4j
@Component
public class AaregConsumer {

    private final RestTemplate restTemplate;
    private final String aaregUrl;

    public AaregConsumer(RestTemplate restTemplate, @Value("${testnorge-aareg.rest.api.url}") String aaregUrl) {
        this.restTemplate = restTemplate;
        this.aaregUrl = aaregUrl + "/api/v1";
    }

    public void send(Set<AaregModel> data, String environment) {

    }
}
