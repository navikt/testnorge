package no.nav.registre.testnorge.helsepersonell.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.testnorge.helsepersonell.consumer.command.GetSamhandlerCommand;
import no.nav.registre.testnorge.helsepersonell.domain.Samhandler;

@Slf4j
@Component
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

    public CompletableFuture<Samhandler> getSamhandler(String ident) {
        return CompletableFuture.supplyAsync(
                () -> new Samhandler(new GetSamhandlerCommand(url, ident, restTemplate).call()),
                executor
        );
    }
}

