package no.nav.registre.orkestratoren.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.common.command.GetPersonCommand;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import no.nav.testnav.libs.dto.person.v1.Persondatasystem;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.AzureClientCredentials;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;

@Slf4j
@Component
public class PersonConsumer {
    private final WebClient webClient;
    private final AzureClientCredentials clientCredential;
    private final ClientCredentialGenerateAccessTokenService accessTokenService;
    private final Executor executor;

    public PersonConsumer(
            @Value("${consumers.person.url}") String baseUrl,
            ObjectMapper objectMapper, @Value("${consumers.person.threads}") Integer threads,
            AzureClientCredentials clientCredential,
            ClientCredentialGenerateAccessTokenService accessTokenService
    ) {
        this.clientCredential = clientCredential;
        this.accessTokenService = accessTokenService;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(baseUrl)
                .build();
        this.executor = Executors.newFixedThreadPool(threads);
    }

    private CompletableFuture<PersonDTO> getPerson(String ident) {
        AccessToken accessToken = accessTokenService.generateToken(
                clientCredential,
                new AccessScopes("api://" + clientCredential.getClientId() + "/.default")
        );
        return CompletableFuture.supplyAsync(
                () -> new GetPersonCommand(webClient, ident, accessToken.getTokenValue(), Persondatasystem.TPS, "q2").call(),
                executor
        );
    }

    public Set<PersonDTO> getPersoner(Set<String> identer) {
        var futures = identer.stream().map(this::getPerson).collect(Collectors.toList());
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                log.error("Klarte ikke å hente ut person", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}