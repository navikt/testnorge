package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Service
public class EIAService implements IService {

    @Value("${synth-eia-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-eia-url}")
    private String synthEiaUrl;

    private final RestTemplate restTemplate;

    public EIAService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-eia" })
    public List<String> generateSykemeldingerFromNAIS(List<Map<String, String>> request) {
        List<String> result = restTemplate.postForObject(synthEiaUrl, request, List.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
