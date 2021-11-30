package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ClientCredential;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Slf4j
@RequiredArgsConstructor
class ClientCredentialExchangeCommand extends ExchangeCommand {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final ServerProperties serverProperties;

    @Override
    public Mono<AccessToken> call() {
        log.trace("Henter OAuth2 access token fra client credential...");

        var scope = String.join(" ", toScope(serverProperties));
        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredential.getClientId())
                .with("client_secret", clientCredential.getClientSecret())
                .with("grant_type", "client_credentials");

        log.trace("Access token opprettet for OAuth 2.0 Client Credentials flow.");
        return webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                        .doBeforeRetry(value -> log.warn("Prøver å opprette tilkobling til azure på nytt."))
                ).doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved henting av access token for {}. Feilmelding: {}.",
                                scope,
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        )
                )
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException),
                        throwable -> log.error("Feil ved henting av access token for {}", scope, throwable)
                );
    }
}
