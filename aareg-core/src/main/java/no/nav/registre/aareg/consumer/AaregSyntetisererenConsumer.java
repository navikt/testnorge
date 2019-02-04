package no.nav.registre.aareg.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AaregSyntetisererenConsumer {

    private String serverUrl;

    private RestTemplate restTemplate;

    public AaregSyntetisererenConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.serverUrl = syntrestServerUrl;
    }


    public void getSyntetiserteMeldinger() {

    }
}
