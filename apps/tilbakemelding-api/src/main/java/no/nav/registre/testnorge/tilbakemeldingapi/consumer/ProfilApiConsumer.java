package no.nav.registre.testnorge.tilbakemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.tilbakemeldingapi.config.Consumers;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ProfilApiConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public ProfilApiConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getProfilApi();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<ProfilDTO> getBruker() {
        log.info("Henter bruker fra Azure.");
        return tokenExchange
                .exchange(serverProperties)
                .map(AccessToken::getTokenValue)
                .flatMap(token -> webClient
                        .get()
                        .uri("/api/v1/profil")
                        .headers(WebClientHeader.bearer(token))
                        .retrieve()
                        .bodyToMono(ProfilDTO.class)
                        .retryWhen(WebClientError.is5xxException()));
    }

}