package no.nav.registre.testnorge.tilbakemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessScopes;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.registre.testnorge.tilbakemeldingapi.config.credentials.ProfilServiceProperties;

@Slf4j
@Component
public class ProfilApiConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public ProfilApiConsumer(
            ProfilServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes(properties);
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public ProfilDTO getBruker() {
        log.info("Henter bruker fra Azure.");
        return accessTokenService.generateToken(accessScopes).flatMap(accessToken -> webClient.get()
                .uri("/api/v1/profil")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(ProfilDTO.class)
        ).block();
    }
}