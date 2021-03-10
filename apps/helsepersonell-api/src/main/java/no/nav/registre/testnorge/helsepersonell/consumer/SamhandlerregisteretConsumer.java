package no.nav.registre.testnorge.helsepersonell.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.helsepersonell.consumer.command.GetSamhandlerCommand;
import no.nav.registre.testnorge.helsepersonell.domain.Samhandler;

@Slf4j
@Component
public class SamhandlerregisteretConsumer {
    private final Executor executor;
    private final WebClient webClient;

    public SamhandlerregisteretConsumer(
            @Value("${samhandlerregisteret.api.url}") String url,
            @Value("${samhandlerregisteret.api.threads}") Integer threads
    ) {

        this.webClient = WebClient
                .builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .baseUrl(url)
                .build();
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public CompletableFuture<List<Samhandler>> getSamhandler(String ident) {
        return CompletableFuture.supplyAsync(
                () -> Arrays
                        .stream(new GetSamhandlerCommand(ident, webClient).call())
                        .map(Samhandler::new)
                        .collect(Collectors.toList()),
                executor
        );
    }
}

