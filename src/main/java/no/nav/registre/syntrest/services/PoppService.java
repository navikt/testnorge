package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PoppService implements IService {

    @Value("${synth-popp-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-popp-url}")
    private String synthPoppUrl;

    private final RestTemplate restTemplate;

    public PoppService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-popp" })
    public Object getDataFromNAIS(Object fnrs) {
        Object result = restTemplate.postForObject(synthPoppUrl, fnrs, List.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
