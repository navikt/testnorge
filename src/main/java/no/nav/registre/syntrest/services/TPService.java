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
public class TPService {
    private final RestTemplate restTemplate;

    public TPService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<List<Map<String, String>>> generateTPFromNAIS(int num_to_generate) throws InterruptedException{
        List<Map<String, String>> result = restTemplate.getForObject(String.format(NaisConnections.CONNECTION_TP, num_to_generate), List.class);
        return CompletableFuture.completedFuture(result);
    }
}
