/*
package no.nav.registre.syntrest.services;

import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class BAService extends KubernetesUtils implements IService {
    @Value("${synth-ba-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-ba-url}")
    private String synthBaUrl;

    private final RestTemplate restTemplate;

    public BAService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<Object> getDataFromNAIS(Object fnrs) throws InterruptedException {
        Object result = restTemplate.postForObject(synthBaUrl, fnrs, Object.class);
        System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
*/
