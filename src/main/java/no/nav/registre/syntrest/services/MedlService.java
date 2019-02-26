package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MedlService implements IService {

    @Value("${synth-medl-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-medl-url}")
    private String synthMedlUrl;

    private final RestTemplate restTemplate;

    public MedlService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-medl" })
    public Object getDataFromNAIS(Object numToGenerate) {
        Object result = restTemplate.getForObject(String.format(synthMedlUrl, numToGenerate), List.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
