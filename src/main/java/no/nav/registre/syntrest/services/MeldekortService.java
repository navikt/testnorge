package no.nav.registre.syntrest.services;
import no.nav.registre.syntrest.globals.NaisConnections;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MeldekortService {
    private final RestTemplate restTemplate;

    public MeldekortService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<List<String>> generateMeldekortFromNAIS(int num_to_generate, String meldegruppe) {
        List<String> result = restTemplate.getForObject(String.format(NaisConnections.CONNECTION_ARENA_MELDEKORT, num_to_generate, meldegruppe), List.class);
        return CompletableFuture.completedFuture(result);
    }

    public String isAlive(){
        return restTemplate.getForObject(NaisConnections.ALIVE_ARENA_MELDEKORT, String.class);
    }
}
