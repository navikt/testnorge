package no.nav.registre.syntrest.services;

import no.nav.registre.syntrest.domain.Inntektsmelding;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

    @Async
    public CompletableFuture<Map<String, List<Inntektsmelding>>> generateInntektsmeldingerFromNAIS(String[] fnrs) throws InterruptedException {
        Map<String, List<Inntektsmelding>> result = restTemplate.postForObject(synthArenaInntektUrl, fnrs, Map.class);
        System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}

