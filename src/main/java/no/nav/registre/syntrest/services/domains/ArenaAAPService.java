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
public class ArenaAAPService implements IService {
    @Value("${synth-arena-aap-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-arena-aap-115-url}")
    private String synthAap115Url;

    @Value("${synth-arena-aap-nyRettighet-url}")
    private String synthAapNyRettighetUrl;

    private final RestTemplate restTemplate;

    public ArenaAAPService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-arena-aap" })
    public Object getDataFromNAIS(Object requestParams) {
        Map<String, String> rp = (Map) requestParams;
        if (rp.get("type") == "115"){
            return restTemplate.getForObject(String.format(synthAap115Url, rp.get("numToGenerate")), List.class);
        } else {
            return restTemplate.getForObject(String.format(synthAapNyRettighetUrl, rp.get("numToGenerate")), List.class);
        }
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
