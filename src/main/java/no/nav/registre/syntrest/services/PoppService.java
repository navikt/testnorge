package no.nav.registre.syntrest.services;

import no.nav.registre.syntrest.globals.NaisConnections;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PoppService {
    private final RestTemplate restTemplate;

    public PoppService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<List<Map<String, Object>>> generatePoppMeldingerFromNAIS(String[] fnrs) throws InterruptedException {
        List<Map<String, Object>> result = restTemplate.postForObject(NaisConnections.CONNECTION_POPP, fnrs, List.class);
        System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(NaisConnections.ALIVE_POPP, String.class);
    }
}
