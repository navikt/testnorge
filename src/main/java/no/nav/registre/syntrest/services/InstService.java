package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class InstService implements IService {

    @Value("${synth-inst-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-inst-url}")
    private String synthInstUrl;

    private final RestTemplate restTemplate;

    public InstService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-inst" })
    public Object getDataFromNAIS(Object numtToGenerate) {
        Object result = restTemplate.getForObject(String.format(synthInstUrl, numtToGenerate), List.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
