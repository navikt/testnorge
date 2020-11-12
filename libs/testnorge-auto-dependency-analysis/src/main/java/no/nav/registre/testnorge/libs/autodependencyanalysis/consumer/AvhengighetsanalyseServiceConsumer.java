package no.nav.registre.testnorge.libs.autodependencyanalysis.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.autodependencyanalysis.config.AvhengighetsanalyseServiceClientCredential;
import no.nav.registre.testnorge.libs.autodependencyanalysis.consumer.command.RegisterApplicationCommand;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;

@Slf4j
@Component
public class AvhengighetsanalyseServiceConsumer {
    private final WebClient webClient;
    private final AvhengighetsanalyseServiceClientCredential clientCredential;
    private final ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService;

    public AvhengighetsanalyseServiceConsumer(
            AvhengighetsanalyseServiceClientCredential clientCredential,
            ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService
    ) {
        this.clientCredential = clientCredential;
        this.clientCredentialGenerateAccessTokenService = clientCredentialGenerateAccessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(clientCredential.getHost())
                .build();
    }

    public void registerApplication(String name) {
        AccessToken accessToken = clientCredentialGenerateAccessTokenService.generateToken(
                clientCredential,
                new AccessScopes("api://" + clientCredential.getClientId() + "/.default")
        );
        new RegisterApplicationCommand(webClient, name, accessToken.getTokenValue()).run();
        log.info("Registert application {} avheningheter.", name);
    }
}
