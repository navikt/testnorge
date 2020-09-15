package no.nav.registre.testnorge.profil.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.OnBehalfOfGenerateAccessTokenService;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileCommand;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileImageCommand;
import no.nav.registre.testnorge.profil.consumer.credentials.ProfilApiClientCredential;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;
import no.nav.registre.testnorge.profil.domain.Profil;

@Component
public class AzureAdProfileConsumer {

    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final OnBehalfOfGenerateAccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public AzureAdProfileConsumer(
            @Value("${api.azuread.url}") String url,
            ProfilApiClientCredential clientCredential,
            OnBehalfOfGenerateAccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes(url + "/.default");
        this.clientCredential = clientCredential;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public Profil getProfil() {
        AccessToken accessToken = accessTokenService.generateToken(clientCredential, accessScopes);
        ProfileDTO dto = new GetProfileCommand(webClient, accessToken.getTokenValue()).call();
        return new Profil(dto);
    }

    public byte[] getProfilImage() {
        AccessToken accessToken = accessTokenService.generateToken(clientCredential, accessScopes);
        ByteArrayResource resource = new GetProfileImageCommand(webClient, accessToken.getTokenValue()).call();
        return resource.getByteArray();
    }

}