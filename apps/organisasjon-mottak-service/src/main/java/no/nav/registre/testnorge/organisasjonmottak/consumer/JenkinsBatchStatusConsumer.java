package no.nav.registre.testnorge.organisasjonmottak.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonmottak.config.properties.JenkinsBatchStatusServiceProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterEregBestillingCommand;

@Component
public class JenkinsBatchStatusConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public JenkinsBatchStatusConsumer(
            JenkinsBatchStatusServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.clientId = properties.getClientId();
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public void registerBestilling(String uuid, String miljo, Long itemId) {
        var accessToken = accessTokenService.generateClientCredentialAccessToken(clientId);
        new RegisterEregBestillingCommand(webClient, accessToken.getTokenValue(), uuid, miljo, itemId).run();
    }
}
