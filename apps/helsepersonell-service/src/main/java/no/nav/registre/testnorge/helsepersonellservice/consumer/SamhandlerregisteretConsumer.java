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
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class SamhandlerregisteretConsumer {
    private final Executor executor;
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final SamhandlerregisteretServerProperties serverProperties;

    public SamhandlerregisteretConsumer(
            AccessTokenService accessTokenService,
            SamhandlerregisteretServerProperties serverProperties
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;
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
        var accessToken = accessTokenService.generateToken(serverProperties);
        return CompletableFuture.supplyAsync(
                () -> Arrays
                        .stream(new GetSamhandlerCommand(ident, webClient, accessToken.getTokenValue()).call())
                        .map(Samhandler::new)
                        .collect(Collectors.toList()),
                executor
        );
    }
}

