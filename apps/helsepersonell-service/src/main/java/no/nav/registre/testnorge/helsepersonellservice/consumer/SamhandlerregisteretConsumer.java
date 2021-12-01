package no.nav.registre.testnorge.helsepersonellservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.helsepersonellservice.config.credentials.SamhandlerregisteretServerProperties;
import no.nav.registre.testnorge.helsepersonellservice.consumer.command.GetSamhandlerCommand;
import no.nav.registre.testnorge.helsepersonellservice.domain.Samhandler;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class SamhandlerregisteretConsumer {
    private final Executor executor;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final SamhandlerregisteretServerProperties serverProperties;

    public SamhandlerregisteretConsumer(
            TokenExchange tokenExchange,
            SamhandlerregisteretServerProperties serverProperties
    ) {
        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .baseUrl(serverProperties.getUrl())
                .build();
        this.executor = Executors.newFixedThreadPool(serverProperties.getThreads());
    }

    public CompletableFuture<List<Samhandler>> getSamhandler(String ident) {
        var accessToken = tokenExchange.generateToken(serverProperties).block();
        return CompletableFuture.supplyAsync(
                () -> Arrays
                        .stream(new GetSamhandlerCommand(ident, webClient, accessToken.getTokenValue()).call())
                        .map(Samhandler::new)
                        .collect(Collectors.toList()),
                executor
        );
    }
}

