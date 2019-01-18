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
public class EIAService {

    private final RestTemplate restTemplate;

    public EIAService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<List<String>> generateSykemeldingerFromNAIS(List<Map<String, String>> request) {
        List<String> result = restTemplate.postForObject(NaisConnections.CONNECTION_EIA, request, List.class);
        return CompletableFuture.completedFuture(result);
    }
}
