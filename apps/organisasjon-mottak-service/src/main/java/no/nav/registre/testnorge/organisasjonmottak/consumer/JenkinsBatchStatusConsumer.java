package no.nav.registre.testnorge.organisasjonmottak.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonmottak.config.properties.JenkinsBatchStatusServiceProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterEregBestillingCommand;

@Component
public class JenkinsBatchStatusConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public JenkinsBatchStatusConsumer(
            JenkinsBatchStatusServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public void registerBestilling(String uuid, String miljo, Long itemId) {
        var accessToken = accessTokenService.generateClientCredentialAccessToken(properties);
        new RegisterEregBestillingCommand(webClient, accessToken.getTokenValue(), uuid, miljo, itemId).run();
    }
}
