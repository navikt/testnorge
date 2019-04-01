package no.nav.registre.sdForvalter.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Set;

@Component
public class SkdConsumer {

    private final RestTemplate restTemplate;
    private final String skdUrl;

    public SkdConsumer(RestTemplate restTemplate, /**@Value("${testnorge-skd.rest.api.url}")*/@Value("abc") String skdUrl) {
        this.restTemplate = restTemplate;
        this.skdUrl = skdUrl + "/v1";
    }

    public Set<String> createTpsMessages() {
        return Collections.emptySet();
    }

}
