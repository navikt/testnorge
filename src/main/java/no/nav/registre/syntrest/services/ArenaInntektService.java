package no.nav.registre.syntrest.services;

import no.nav.registre.syntrest.Domain.Inntektsmelding;
import no.nav.registre.syntrest.globals.NaisConnections;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ArenaInntektService {

    private final RestTemplate restTemplate;

    public ArenaInntektService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<Map<String, List<Inntektsmelding>>> generateInntektsmeldinger(String[] fnrs) throws InterruptedException {
        Map<String, List<Inntektsmelding>> result = restTemplate.postForObject(NaisConnections.CONNECTION_ARENA_INNTEKT, fnrs, Map.class);
        System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }
}

