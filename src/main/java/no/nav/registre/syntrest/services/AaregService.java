package no.nav.registre.syntrest.services;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    @Async
    public CompletableFuture<String> generateAaregFromNAIS(List<String> request) {
        String result = restTemplate.postForObject(synthAaregUrl, request, String.class);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}