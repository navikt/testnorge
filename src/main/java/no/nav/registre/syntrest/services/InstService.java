package no.nav.registre.syntrest.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    @Async
    public CompletableFuture<List<Map<String, String>>> generateInstFromNAIS(int num_to_generate) throws InterruptedException {
        List<Map<String, String>> result = restTemplate.getForObject(String.format(synthInstUrl, num_to_generate), List.class);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
