package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.syntrest.domain.Inntektsmelding;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ArenaInntektService extends KubernetesUtils implements IService {

    @Value("${synth-arena-inntekt-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-arena-inntekt-url}")
    private String synthArenaInntektUrl;

    private final RestTemplate restTemplate;

    public ArenaInntektService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-arena-inntekt" })
    public Map<String, List<Inntektsmelding>> generateInntektsmeldingerFromNAIS(String[] fnrs) throws InterruptedException {
        Map<String, List<Inntektsmelding>> result = restTemplate.postForObject(synthArenaInntektUrl, fnrs, Map.class);
        return result;
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}

