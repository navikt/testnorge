package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ArenaAAPService implements IService{
    @Value("${synth-arena-aap-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-arena-aap-url}")
    private String synthAapUrl;

    private final RestTemplate restTemplate;

    public ArenaAAPService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-arena-aap" })
    public List<Map<String, String>> generateArenaAAPFromNAIS(int numToGenerate) {
        List<Map<String, String>> result = restTemplate.getForObject(String.format(synthAapUrl, numToGenerate), List.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
