package no.nav.registre.testnorge.libs.oauth2.service;

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

import no.nav.registre.testnorge.libs.oauth2.config.Scopeable;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.AzureClientCredentials;

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

    @Deprecated
    public AccessToken generateToken(String clientId) {
        return generateToken(new AccessScopes("api://" + clientId + "/.default"));
    }

    public AccessToken generateToken(Scopeable scopeable) {
        return generateToken(new AccessScopes(scopeable.toScope()));
    }

    public AccessToken generateToken(AccessScopes accessScopes) {
        try {
            return generateNonBlockedToken(accessScopes).block();
        } catch (WebClientResponseException e) {
            var secret = clientCredentials.getClientSecret().substring(0, 3) + "******************";
            log.error(
                    "Feil ved henting av access token for {} med client secret: {}.\nError: \n{}.",
                    String.join(" ", accessScopes.getScopes()),
                    secret,
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }


    public Mono<AccessToken> generateNonBlockedToken(Scopeable scopeable) {
        return generateNonBlockedToken(new AccessScopes(scopeable));
    }

    public Mono<AccessToken> generateNonBlockedToken(AccessScopes accessScopes) {
        tokenResolver.verifyAuthentication();

        if (accessScopes.getScopes().isEmpty()) {
            throw new RuntimeException("Kan ikke opprette accessToken uten scopes (clienter).");
        }

        if (tokenResolver.isClientCredentials()) {
            return generateClientCredentialAccessToken(accessScopes);
        }

        return generateOnBehalfOfAccessToken(accessScopes);
    }


    /**
     * Skal kun brukes av operasjoner startet av batcher/kafka.
     *
     * @param clientId appen som skal kontaktes
     * @return access token som tilsvarer appen som skal kontaktes.
     */
    @Deprecated
    public AccessToken generateClientCredentialAccessToken(String clientId) {
        return generateClientCredentialAccessToken(new AccessScopes("api://" + clientId + "/.default")).block();
    }


    public AccessToken generateClientCredentialAccessToken(Scopeable serverProperties) {
        return generateClientCredentialAccessToken(new AccessScopes(serverProperties.toScope())).block();
    }


    private Mono<AccessToken> generateClientCredentialAccessToken(AccessScopes accessScopes) {
        log.trace("Henter OAuth2 access token fra client credential...");

        var body = BodyInserters
                .fromFormData("scope", String.join(" ", accessScopes.getScopes()))
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("grant_type", "client_credentials");

        try {
            var token = webClient.post()
                    .body(body)
                    .retrieve()
                    .bodyToMono(AccessToken.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                            .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                            .doBeforeRetry(value -> log.warn("Prøver å opprette tilbobling til azure på nytt."))
                    );
            log.trace("Access token opprettet for OAuth 2.0 Client Credentials flow.");
            return token;
        } catch (WebClientResponseException e) {
            log.error(
                    "Feil ved henting av access token for {}. Feilmelding: {}.",
                    String.join(" ", accessScopes.getScopes()),
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }

    private Mono<AccessToken> generateOnBehalfOfAccessToken(AccessScopes accessScopes) {
        String oid = tokenResolver.getOid();
        if (oid != null) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            contextMap.put("oid", oid);
            MDC.setContextMap(contextMap);
        }

        var token = tokenResolver.getTokenValue();


        if (clientCredentials.getClientSecret() == null) {
            log.error("Client secret er null.");
        }

        var scopes = String.join(" ", accessScopes.getScopes());
        var body = BodyInserters
                .fromFormData("scope", scopes)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("assertion", token)
                .with("requested_token_use", "on_behalf_of")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        var accessToken = webClient.post()
                .body(body)
                .exchange()
                .flatMap(response -> response.bodyToMono(AccessToken.class))
                .doOnError(error -> log.error("Feil ved henting av access token.", error));

        log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow. \nScopes: {}.", scopes);
        return accessToken;
    }
}
