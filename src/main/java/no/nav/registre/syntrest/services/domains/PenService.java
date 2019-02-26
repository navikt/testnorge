package no.nav.registre.syntrest.services.domains;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PenService implements IService {

    @Value("${synth-pen-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-pen-url}")
    private String synthPenUrl;

    private final RestTemplate restTemplate;

    public PenService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-pen" })
    public Object getDataFromNAIS(Object numToGenerate) {
        Object result = restTemplate.getForObject(String.format(synthPenUrl, numToGenerate), Object.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }

}
