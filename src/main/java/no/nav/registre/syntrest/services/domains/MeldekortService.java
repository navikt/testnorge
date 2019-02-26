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
public class MeldekortService implements IService {

    @Value("${synth-arena-meldekort-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-arena-meldekort-url}")
    private String synthArenaMeldekortUrl;

    private final RestTemplate restTemplate;

    public MeldekortService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-meldekort" })
    public Object getDataFromNAIS(Object requestParams) {
        Map<String, String> rp = (Map) requestParams;
        Object result = restTemplate.getForObject(String.format(synthArenaMeldekortUrl, rp.get("numToGenerate"), rp.get("meldegruppe")), List.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
