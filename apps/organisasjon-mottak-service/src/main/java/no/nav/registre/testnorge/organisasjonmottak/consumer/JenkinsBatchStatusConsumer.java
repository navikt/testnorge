package no.nav.registre.testnorge.organisasjonmottak.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterEregBestillingCommand;

@Component
public class JenkinsBatchStatusConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public JenkinsBatchStatusConsumer(
            @Value("${consumers.jenkins-batch-status-service.url}") String url,
            @Value("${consumers.jenkins-batch-status-service.client-id}") String clientId,
            AccessTokenService accessTokenService
    ) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public Mono<Void> registerBestilling(String uuid, String miljo, Long itemId) {
        var accessToken = accessTokenService.generateClientCredentialAccessToken(clientId);
        return new RegisterEregBestillingCommand(webClient, accessToken.getTokenValue(), uuid, miljo, itemId).call();
    }
}
