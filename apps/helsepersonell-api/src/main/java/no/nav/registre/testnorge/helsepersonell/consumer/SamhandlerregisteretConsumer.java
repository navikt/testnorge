package no.nav.registre.testnorge.helsepersonell.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.helsepersonell.consumer.command.GetSamhandlerCommand;
import no.nav.registre.testnorge.helsepersonell.domain.Samhandler;

@Slf4j
@Component
@DependencyOn(value = "samhandlerregisteret", external = true)
public class SamhandlerregisteretConsumer {
    private final String url;
    private final RestTemplate restTemplate;
    private final Executor executor;

    public SamhandlerregisteretConsumer(
            @Value("${samhandlerregisteret.api.url}") String url,
            @Value("${samhandlerregisteret.api.threads}") Integer threads,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public CompletableFuture<List<Samhandler>> getSamhandler(String ident) {
        return CompletableFuture.supplyAsync(
                () -> Arrays
                        .stream(new GetSamhandlerCommand(url, ident, restTemplate).call())
                        .map(Samhandler::new)
                        .collect(Collectors.toList()),
                executor
        );
    }
}

