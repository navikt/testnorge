package no.nav.registre.syntrest.services;

import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.domain.Inntektsmelding;
import no.nav.registre.syntrest.globals.NaisConnections;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ArenaInntektService extends KubernetesUtils {

    private final RestTemplate restTemplate;

    public ArenaInntektService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<Map<String, List<Inntektsmelding>>> generateInntektsmeldingerFromNAIS(String[] fnrs) throws InterruptedException, IOException, ApiException {

        Map<String, List<Inntektsmelding>> result = restTemplate.postForObject(NaisConnections.CONNECTION_ARENA_INNTEKT, fnrs, Map.class);
        System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(NaisConnections.ALIVE_ARENA_INNTEKT, String.class);
    }
}

