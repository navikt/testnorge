package no.nav.testnav.libs.servletsecurity.exchange.tokenx.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.servletsecurity.domain.AccessToken;

@Slf4j
@RequiredArgsConstructor
public class GetAccessTokenCommand implements Callable<Mono<AccessToken>> {
    private final WebClient webClient;
    private final String url;
    private final String clientAssertion;
    private final String token;
    private final String scope;

    @Override
    public Mono<AccessToken> call() {
        return webClient
                .post()
                .uri(url)
                .body(BodyInserters
                        .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                        .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                        .with("client_assertion", clientAssertion)
                        .with("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
                        .with("subject_token", token)
                        .with("audience", scope)
                ).retrieve()
                .bodyToMono(AccessToken.class)
                .doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved henting av access token. Feilmelding: {}.",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                        ))
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException),
                        throwable -> log.error("Feil ved henting av access token.", throwable)
                );
    }
}
