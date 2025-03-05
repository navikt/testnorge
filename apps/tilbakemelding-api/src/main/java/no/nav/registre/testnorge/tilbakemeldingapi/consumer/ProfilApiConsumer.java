package no.nav.registre.testnorge.tilbakemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.tilbakemeldingapi.config.Consumers;
import no.nav.registre.testnorge.tilbakemeldingapi.util.WebClientFilter;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

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

    public ProfilDTO getBruker() {
        log.info("Henter bruker fra Azure.");
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> webClient.get()
                        .uri("/api/v1/profil")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                        .retrieve()
                        .bodyToMono(ProfilDTO.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException))
                ).block();
    }
}