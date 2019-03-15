package no.nav.registre.syntrest.services.domains;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class InntektService extends KubernetesUtils implements IService {

    @Value("${synth-inntekt-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-inntekt-url}")
    private String synthInntektUrl;

    private final RestTemplate restTemplate;

    public InntektService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-inntekt" })
    public Object getDataFromNAIS(Object fnrs) {
        Object result = restTemplate.postForObject(synthInntektUrl, fnrs, Map.class);
        return result;
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}

