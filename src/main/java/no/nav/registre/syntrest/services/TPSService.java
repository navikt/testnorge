package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class TPSService implements IService{

    @Value("${synth-tps-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-tps-url}")
    private String synthTpsUrl;

    private final RestTemplate restTemplate;

    public TPSService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-tps" })
    @Async
    public CompletableFuture<List<Map<String, Object>>> generateTPSFromNAIS(int num_to_generate, String endringskode) throws InterruptedException{
        List<Map<String, Object>> result = restTemplate.getForObject(String.format(synthTpsUrl, num_to_generate, endringskode), List.class);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
