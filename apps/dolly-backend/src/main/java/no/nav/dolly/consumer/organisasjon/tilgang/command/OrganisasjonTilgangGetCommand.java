package no.nav.dolly.consumer.organisasjon.tilgang.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.organisasjon.tilgang.dto.OrganisasjonTilgang;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class OrganisasjonTilgangGetCommand implements Callable<Flux<OrganisasjonTilgang>> {

    private static final String ORGANISASJONER_URL = "/api/v1/organisasjoner";

    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<OrganisasjonTilgang> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(ORGANISASJONER_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(OrganisasjonTilgang.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
