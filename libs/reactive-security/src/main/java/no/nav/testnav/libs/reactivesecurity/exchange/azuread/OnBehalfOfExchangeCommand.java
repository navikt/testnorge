package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ClientCredential;
import no.nav.testnav.libs.reactivesecurity.domain.Token;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Slf4j
@RequiredArgsConstructor
class OnBehalfOfExchangeCommand extends ExchangeCommand {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final ServerProperties serverProperties;
    private final Token token;

    @Override
    public Mono<AccessToken> call() {
        var scope = toScope(serverProperties);
        String oid = token.getUserId();
        if (oid != null) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            contextMap.put("oid", oid);
            MDC.setContextMap(contextMap);
        }

        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredential.getClientId())
                .with("client_secret", clientCredential.getClientSecret())
                .with("assertion", token.getValue())
                .with("requested_token_use", "on_behalf_of")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow. Scope: {}.", scope);
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
