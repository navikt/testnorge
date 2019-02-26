package no.nav.registre.syntrest.services.domains;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AaregService implements IService {

    @Value("${synth-aareg-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-aareg-url}")
    private String synthAaregUrl;

    private final RestTemplate restTemplate;

    public AaregService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-aareg" })
    public Object getDataFromNAIS(Object request) {
        Object result = restTemplate.postForObject(synthAaregUrl, request, String.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}