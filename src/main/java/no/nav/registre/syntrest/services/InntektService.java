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

    @Async
    public CompletableFuture<Map<String, List<Inntektsmelding>>> generateInntektsmeldingerFromNAIS(String[] fnrs) throws InterruptedException {
        Map<String, List<Inntektsmelding>> result = restTemplate.postForObject(synthInntektUrl, fnrs, Map.class);
        System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}

