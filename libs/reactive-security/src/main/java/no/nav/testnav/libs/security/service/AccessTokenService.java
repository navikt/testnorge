package no.nav.testnav.libs.security.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import reactor.netty.tcp.ProxyProvider;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import no.nav.testnav.libs.security.domain.AccessScopes;
import no.nav.testnav.libs.security.domain.AccessToken;
import no.nav.testnav.libs.security.domain.AzureClientCredentials;
import no.nav.testnav.libs.security.domain.Scopeable;
import no.nav.testnav.libs.security.domain.Token;

@Slf4j
@Service
public class AccessTokenService {
    private final WebClient webClient;
    private final AuthenticationTokenResolver tokenResolver;
    private final AzureClientCredentials clientCredentials;

    public AccessTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            AuthenticationTokenResolver tokenResolver,
            AzureClientCredentials clientCredentials
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
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort())
                    ));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
        this.tokenResolver = tokenResolver;
        this.webClient = builder.build();
        this.clientCredentials = clientCredentials;
    }


    public Mono<AccessToken> generateToken(Scopeable scopeable) {
        return generateToken(new AccessScopes(scopeable));
    }

    public Mono<AccessToken> generateToken(AccessScopes accessScopes) {

        if (accessScopes.getScopes().isEmpty()) {
            throw new RuntimeException("Kan ikke opprette accessToken uten scopes (clienter).");
        }

        return tokenResolver
                .getToken()
                .flatMap(token -> {
                    if (token.isClientCredentials()) {
                        return generateClientCredentialAccessToken(accessScopes);
                    }
                    return generateOnBehalfOfAccessToken(token, accessScopes);
                });
    }

    private Mono<AccessToken> generateClientCredentialAccessToken(AccessScopes accessScopes) {
        log.trace("Henter OAuth2 access token fra client credential...");

        var scope = String.join(" ", accessScopes.getScopes());
        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("grant_type", "client_credentials");

        log.trace("Access token opprettet for OAuth 2.0 Client Credentials flow.");
        return webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                        .doBeforeRetry(value -> log.warn("Prøver å opprette tilkobling til azure på nytt."))
                ).doOnError(error -> {
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

    private Mono<AccessToken> generateOnBehalfOfAccessToken(Token token, AccessScopes accessScopes) {
        String oid = token.getOid();
        if (oid != null) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            contextMap.put("oid", oid);
            MDC.setContextMap(contextMap);
        }

        if (clientCredentials.getClientSecret() == null) {
            log.error("Client secret er null.");
        }

        var scope = String.join(" ", accessScopes.getScopes());

        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("assertion", token.getValue())
                .with("requested_token_use", "on_behalf_of")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow. \nScope: {}.", scope);
        return webClient
                .post()
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
