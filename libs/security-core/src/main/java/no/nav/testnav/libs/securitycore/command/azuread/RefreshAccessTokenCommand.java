package no.nav.testnav.libs.securitycore.command.azuread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.command.ExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class RefreshAccessTokenCommand implements ExchangeCommand {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final String scope;
    private final String refreshToken;

    @Override
    public Mono<AccessToken> call() {
        log.trace("Henter OAuth2 access token fra refresh token...");
        var body = BodyInserters
                .fromFormData("scope", "openid")
                .with("client_id", clientCredential.getClientId())
                .with("client_secret", clientCredential.getClientSecret())
                .with("refresh_token", refreshToken)
                .with("grant_type", "refresh_token");

        log.trace("Access token opprettet for OAuth 2.0. ved bruk av refresh token");
        return webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                        .doBeforeRetry(value -> log.warn("Prøver å opprette tilkobling til azure på nytt.")))
                .doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved henting av access token for {}. Feilmelding: {}.",
                                scope,
                                ((WebClientResponseException) throwable).getResponseBodyAsString(),
                                throwable
                        ))
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException),
                        throwable -> log.error("Feil ved henting av access token for {}", scope, throwable)
                );
    }
}
