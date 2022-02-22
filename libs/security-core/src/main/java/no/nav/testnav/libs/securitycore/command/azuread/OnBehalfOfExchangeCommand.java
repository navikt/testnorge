package no.nav.testnav.libs.securitycore.command.azuread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.command.ExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class OnBehalfOfExchangeCommand implements ExchangeCommand {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final String scope;
    private final Token token;

    @Override
    public Mono<AccessToken> call() {
        String oid = token.getUserId();
        if (oid != null) {
            Map<String, String> contextMap = nonNull(MDC.getCopyOfContextMap()) ? MDC.getCopyOfContextMap() : new HashMap<>();
            contextMap.put("oid", oid);
            MDC.setContextMap(contextMap);
        }

        log.info("Scope: {}", scope);

        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredential.getClientId())
                .with("client_secret", clientCredential.getClientSecret())
                .with("assertion", token.getAccessTokenValue())
                .with("requested_token_use", "on_behalf_of")
                .with("connection_scope", "offline_access")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        var refreshBody = BodyInserters
                .fromFormData("scope", scope)
                .with("grant_type", "refresh_token")
                .with("refresh_token", token.getRefreshTokenValue())
                .with("client_id", clientCredential.getClientId())
                .with("client_secret", clientCredential.getClientSecret());

        log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow. Scope: {}.", scope);
        log.info("AccessToken expirer: {}", token.getExpiresAt());
        log.info("RefreshToken: {}", token.getRefreshTokenValue());

        if (ZonedDateTime.now().toInstant().isAfter(token.getExpiresAt())) {
            log.info("Accesstoken expired, prøver å hente nytt");
            return webClient
                    .post()
                    .body(refreshBody)
                    .retrieve()
                    .bodyToMono(AccessToken.class)
                    .doOnError(
                            WebClientResponseException.class::isInstance,
                            throwable -> log.error(
                                    "Feil ved refresh av access token for {}. Feilmelding: {}.",
                                    scope,
                                    ((WebClientResponseException) throwable).getResponseBodyAsString()
                            )
                    )
                    .doOnError(
                            throwable -> !(throwable instanceof WebClientResponseException),
                            throwable -> log.error("Feil ved refresh av access token for {}", scope, throwable)
                    );
        }

        return webClient
                .post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnError(
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
