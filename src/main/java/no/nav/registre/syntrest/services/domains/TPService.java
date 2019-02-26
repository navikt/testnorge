package no.nav.registre.syntrest.services.domains;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TPService implements IService {

    @Value("${synth-tp-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-tp-url}")
    private String synthTpUrl;

    private final RestTemplate restTemplate;

    public TPService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-tp" })
    public Object getDataFromNAIS(Object numToGenerate) {
        Object result = restTemplate.getForObject(String.format(synthTpUrl, numToGenerate), List.class);
        return result;
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
