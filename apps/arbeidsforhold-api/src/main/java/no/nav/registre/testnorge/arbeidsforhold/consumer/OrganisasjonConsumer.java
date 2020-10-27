package no.nav.registre.testnorge.arbeidsforhold.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.arbeidsforhold.credential.OrganisasjonApiClientCredential;
import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;

@Component
public class OrganisasjonConsumer {
    private final ClientCredential clientCredential;
    private final ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService;
    private final WebClient webClient;

    public OrganisasjonConsumer(
            OrganisasjonApiClientCredential clientCredential,
            ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService,
            @Value("${consumers.organisasjonapi.url}") String baseUrl
    ) {
        this.clientCredential = clientCredential;
        this.clientCredentialGenerateAccessTokenService = clientCredentialGenerateAccessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(baseUrl)
                .build();
    }

    public OrganisasjonDTO getOrganisjon(String orgnummer, String miljo) {
        AccessToken accessToken = clientCredentialGenerateAccessTokenService.generateToken(clientCredential);
        return new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call();
    }
}
