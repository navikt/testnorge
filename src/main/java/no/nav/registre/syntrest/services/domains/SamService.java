package no.nav.registre.syntrest.services.domains;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SamService implements IService {
    @Value("${synth-sam-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-sam-url}")
    private String synthSamUrl;

    private final RestTemplate restTemplate;

    public SamService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-sam" })
    public Object getDataFromNAIS(Object numToGenerate) {
        Object result = restTemplate.getForObject(String.format(synthSamUrl, numToGenerate), List.class);
        return result;
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
