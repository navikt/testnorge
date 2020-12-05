package no.nav.registre.testnorge.tilbakemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.profil.v1.ProfilDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
@DependencyOn("testnorge-profil-api")
public class ProfilApiConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public ProfilApiConsumer(
            @Value("${consumer.profil-api.url}") String url,
            @Value("${consumer.profil-api.client-id}") String clientId,
            AccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public ProfilDTO getBruker() {
        AccessToken accessToken = accessTokenService.generateToken(accessScopes);
        log.info("Henter bruker fra Azure.");
        return webClient.get()
                .uri("/api/v1/profil")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(ProfilDTO.class)
                .block();
    }
}