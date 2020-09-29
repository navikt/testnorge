package no.nav.registre.sdforvalter.consumer.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.sdforvalter.credentials.PersonApiClientCredential;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.testnorge.libs.common.command.CreatePersonCommand;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;

@Slf4j
@Component
@DependencyOn("person-api")
public class PersonConsumer {

    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final ClientCredentialGenerateAccessTokenService accessTokenService;
    private final Executor executor;

    public PersonConsumer(
            @Value("${consumers.person.url}") String baseUrl,
            ObjectMapper objectMapper, @Value("${consumers.person.threads}") Integer threads,
            PersonApiClientCredential clientCredential,
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

    public void opprettPersoner(TpsIdentListe identer) {
        AccessToken accessToken = accessTokenService.generateToken(
                clientCredential,
                new AccessScopes("api://" + clientCredential.getClientId() + "/.default")
        );

        identer.stream().forEach(ident ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        new CreatePersonCommand(webClient, ident.toDTO(), accessToken.getTokenValue(), ident.getOpprinnelse()).run();
                    } catch (Exception e) {
                        log.error("Kunne ikke opprette ident {}: {}", ident.getFnr(), e);
                    }
                    return null;
                }, executor)
        );
    }
}
