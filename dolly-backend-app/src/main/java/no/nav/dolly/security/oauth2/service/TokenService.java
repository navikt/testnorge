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
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

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
        return generateClientCredentialAccessToken(new AccessScopes("api://" + clientId + "/.default"));
    }


    public AccessToken generateClientCredentialAccessToken(Scopeable serverProperties) {
        return generateClientCredentialAccessToken(new AccessScopes(serverProperties.toScope()));
    }


    private AccessToken generateClientCredentialAccessToken(AccessScopes accessScopes) {
        log.trace("Henter OAuth2 access token fra client credential...");

        var body = BodyInserters
                .fromFormData("scope", String.join(" ", accessScopes.getScopes()))
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("grant_type", "client_credentials");

        try {
            AccessToken token = webClient.post()
                    .body(body)
                    .retrieve()
                    .bodyToMono(AccessToken.class)
                    .block();
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

    private AccessToken generateOnBehalfOfAccessToken(AccessScopes accessScopes) {

        var token = tokenResolver.getTokenValue();

        if (clientCredentials.getClientSecret() == null) {
            log.error("Client secret er null.");
        }

        var body = BodyInserters
                .fromFormData("scope", String.join(" ", accessScopes.getScopes()))
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("assertion", token)
                .with("requested_token_use", "on_behalf_of")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        try {
            AccessToken accessToken = webClient.post()
                    .body(body)
                    .retrieve()
                    .bodyToMono(AccessToken.class)
                    .block();

            log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow");
            return accessToken;
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
}
