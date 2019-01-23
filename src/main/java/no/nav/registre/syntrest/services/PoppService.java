package no.nav.registre.syntrest.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PoppService implements IService{

    @Value("${isAlive}")
    private String isAlive;

    @Value("${synth-popp-url}")
    private String synthPoppUrl;

    private final RestTemplate restTemplate;

    public PoppService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<List<Map<String, Object>>> generatePoppMeldingerFromNAIS(String[] fnrs) throws InterruptedException {
        List<Map<String, Object>> result = restTemplate.postForObject(synthPoppUrl, fnrs, List.class);
        System.out.println(result);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(String.format(isAlive, "nais-synthdata-popp"), String.class);
    }
}
