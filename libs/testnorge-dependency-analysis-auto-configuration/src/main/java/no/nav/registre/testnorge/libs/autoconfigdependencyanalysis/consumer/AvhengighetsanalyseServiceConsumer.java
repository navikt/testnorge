package no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config.AvhengighetsanalyseServiceClientCredential;
import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.consumer.command.RegisterApplicationCommand;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateWithoutLoginAccessTokenService;

@Slf4j
@Component
public class AvhengighetsanalyseServiceConsumer {
    private final WebClient webClient;
    private final AvhengighetsanalyseServiceClientCredential clientCredential;
    private final ClientCredentialGenerateWithoutLoginAccessTokenService tokenService;

    public AvhengighetsanalyseServiceConsumer(
            AvhengighetsanalyseServiceClientCredential clientCredential,
            ClientCredentialGenerateWithoutLoginAccessTokenService tokenService
    ) {
        this.clientCredential = clientCredential;
        this.tokenService = tokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(clientCredential.getHost())
                .build();
    }

    public void registerApplication(String name) {
        AccessToken accessToken = tokenService.generateToken(
                clientCredential,
                new AccessScopes("api://" + clientCredential.getClientId() + "/.default")
        );
        new RegisterApplicationCommand(webClient, name, accessToken.getTokenValue()).run();
        log.info("Registert app {}.", name);
    }
}
