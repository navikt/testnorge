package no.nav.registre.syntrest.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class PoppService implements IService{

    @Value("${synth-popp-app}")
    private String appName;

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-popp-url}")
    private String synthPoppUrl;

    private final RestTemplate restTemplate;

    public PoppService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public Future generatePoppMeldingerFromNAIS(String[] fnrs) {
        CompletableFuture<List<Map<String, Object>>> completableFuture = new CompletableFuture<>();
        Executors.newFixedThreadPool(10).submit(() -> {
            completableFuture.complete(restTemplate.postForObject(synthPoppUrl, fnrs, List.class));
        });

        return completableFuture;
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, appName), String.class);
    }
}
