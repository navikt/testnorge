package no.nav.registre.testnorge.tilbakemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.tilbakemeldingapi.config.credentials.ProfilServiceProperties;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class ProfilApiConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ProfilServiceProperties properties;

    public ProfilApiConsumer(
            ProfilServiceProperties properties,
            TokenExchange tokenExchange
    ) {
        this.properties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public ProfilDTO getBruker() {
        log.info("Henter bruker fra Azure.");
        return tokenExchange.exchange(properties).flatMap(accessToken -> webClient.get()
                .uri("/api/v1/profil")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(ProfilDTO.class)
        ).block();
    }
}