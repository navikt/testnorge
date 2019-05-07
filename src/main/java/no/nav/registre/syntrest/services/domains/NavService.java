package no.nav.registre.syntrest.services.domains;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NavService implements IService {

    @Value("${synth-nav-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-nav-url}")
    private String synthNavUrl;

    private final RestTemplate restTemplate;

    public NavService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-nav" })
    public Object getDataFromNAIS(Object requestParams) {
        Map<String, String> rp = (Map) requestParams;
        Object result = restTemplate.getForObject(String.format(synthNavUrl, rp.get("numToGenerate"), rp.get("endringskode")), List.class);
        return result;
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
