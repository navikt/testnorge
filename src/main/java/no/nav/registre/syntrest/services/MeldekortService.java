package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
    public List<String> generateMeldekortFromNAIS(int num_to_generate, String meldegruppe) {
        List<String> result = restTemplate.getForObject(String.format(synthArenaMeldekortUrl, num_to_generate, meldegruppe), List.class);
        return result;
    }

    public String isAlive() {
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
