package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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
    public Object getDataFromNAIS(Object requestParams) {
        Map<String, String> rp = (Map) requestParams;
        Object result = restTemplate.getForObject(String.format(synthTpsUrl, rp.get("numToGenerate"), rp.get("endringskode")), List.class);
        return result;
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
