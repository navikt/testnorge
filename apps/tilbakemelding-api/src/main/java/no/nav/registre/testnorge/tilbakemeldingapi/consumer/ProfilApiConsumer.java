package no.nav.registre.testnorge.tilbakemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.profil.v1.ProfilDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.OnBehalfOfGenerateAccessTokenService;
import no.nav.registre.testnorge.tilbakemeldingapi.consumer.credentials.TilbakemeldingApiClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component

public class ProfilApiConsumer {

    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final OnBehalfOfGenerateAccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public ProfilApiConsumer(
            @Value("${consumer.profil-api.url}") String url,
            @Value("${consumer.profil-api.client-id}") String clientId,
            TilbakemeldingApiClientCredential clientCredential,
            OnBehalfOfGenerateAccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
        this.clientCredential = clientCredential;
        this.accessTokenService = accessTokenService;


        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public ProfilDTO getBruker() {
        AccessToken accessToken = accessTokenService.generateToken(clientCredential, accessScopes);
        log.info("Henter bruker");
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/api/v1/profil")
                                .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(ProfilDTO.class)
                .block();

    }
}

