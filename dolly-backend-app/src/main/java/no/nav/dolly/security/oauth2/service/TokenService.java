package no.nav.dolly.security.oauth2.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.security.domain.DollyBackendClientCredential;
import no.nav.dolly.security.oauth2.config.Scopeable;
import no.nav.dolly.security.oauth2.domain.AccessScopes;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

@Slf4j
@Service
public class TokenService {
    private final WebClient webClient;
    private final AuthenticationTokenResolver tokenResolver;
    private final DollyBackendClientCredential clientCredentials;

    public TokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            AuthenticationTokenResolver tokenResolver,
            DollyBackendClientCredential clientCredentials
    ) {
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(issuerUrl + "/oauth2/v2.0/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Client Credentials", proxyHost);
            var uri = URI.create(proxyHost);

            HttpClient httpClient = HttpClient
                    .create()
                    .proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort()));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }

        this.tokenResolver = tokenResolver;
        this.webClient = builder.build();
        this.clientCredentials = clientCredentials;
    }

    public Mono<AccessToken> generateToken(Scopeable scopeable) {
        return generateToken(new AccessScopes(scopeable.toScope()));
    }


    public Mono<AccessToken> generateToken(AccessScopes accessScopes) {
        tokenResolver.verifyAuthentication();

        if (accessScopes.getScopes().isEmpty()) {
            throw new RuntimeException("Kan ikke opprette accessToken uten scopes (clienter).");
        }

        if (tokenResolver.isClientCredentials()) {
            return generateClientCredentialAccessToken(accessScopes);
        }

        return generateOnBehalfOfAccessToken(accessScopes);
    }


    private Mono<AccessToken> generateClientCredentialAccessToken(AccessScopes accessScopes) {
        log.trace("Henter OAuth2 access token fra client credential...");

        var scope = String.join(" ", accessScopes.getScopes());
        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("grant_type", "client_credentials");

        return webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        log.error(
                                "Feil ved henting av access token for {}. Feilmelding: {}.",
                                scope,
                                ((WebClientResponseException) error).getResponseBodyAsString()
                        );
                    } else {
                        log.error("Feil ved henting av access token for {}", scope, error);
                    }
                });

    }

    private Mono<AccessToken> generateOnBehalfOfAccessToken(AccessScopes accessScopes) {

        var token = tokenResolver.getTokenValue();

        if (clientCredentials.getClientSecret() == null) {
            log.error("Client secret er null.");
        }
        var scope = String.join(" ", accessScopes.getScopes());
        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("assertion", token)
                .with("requested_token_use", "on_behalf_of")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        return webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        log.error(
                                "Feil ved henting av access token for {}. Feilmelding: {}.",
                                scope,
                                ((WebClientResponseException) error).getResponseBodyAsString()
                        );
                    } else {
                        log.error("Feil ved henting av access token for {}", scope, error);
                    }
                });

    }
}
