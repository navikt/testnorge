package no.nav.testnav.libs.servletsecurity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

import no.nav.testnav.libs.servletsecurity.domain.AccessScopes;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.servletsecurity.domain.ClientCredential;

@Slf4j
@Service
@Deprecated
public class ClientCredentialGenerateAccessTokenService {
    private final WebClient webClient;
    private final AuthenticationTokenResolver tokenResolver;

    public ClientCredentialGenerateAccessTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            AuthenticationTokenResolver tokenResolver
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
    }

    @Deprecated
    public AccessToken generateToken(ClientCredential remoteClientCredential) {
        return generateToken(remoteClientCredential, new AccessScopes("api://" + remoteClientCredential.getClientId() + "/.default"));
    }

    @Deprecated
    public AccessToken generateToken(ClientCredential remoteClientCredential, AccessScopes accessScopes) {
        if (!tokenResolver.isClientCredentials()) {
            throw new BadCredentialsException("Kan ikke gjennomfore OnBehalfOf-flow fra Client Credentials.");
        }

        if (accessScopes.getScopes().isEmpty()) {
            throw new RuntimeException("Kan ikke opprette accessToken uten scopes (clienter).");
        }

        tokenResolver.verifyAuthentication();

        log.trace("Henter OAuth2 access token fra client credential...");

        var body = BodyInserters
                .fromFormData("scope", String.join(" ", accessScopes.getScopes()))
                .with("client_id", remoteClientCredential.getClientId())
                .with("client_secret", remoteClientCredential.getClientSecret())
                .with("grant_type", "client_credentials");

        AccessToken token = webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .block();
        log.trace("OAuth2 access token hentet.");
        return token;
    }


    public AccessToken generateToken(AzureClientCredentials clientCredentials,  String clientId) {
        return generateToken(clientCredentials, new AccessScopes("api://" + clientId + "/.default"));
    }

    public AccessToken generateToken(AzureClientCredentials clientCredentials, AccessScopes accessScopes) {
        if (!tokenResolver.isClientCredentials()) {
            throw new BadCredentialsException("Kan ikke gjennomfore OnBehalfOf-flow fra Client Credentials.");
        }

        if (accessScopes.getScopes().isEmpty()) {
            throw new RuntimeException("Kan ikke opprette accessToken uten scopes (clienter).");
        }

        tokenResolver.verifyAuthentication();

        log.trace("Henter OAuth2 access token fra client credential...");

        var body = BodyInserters
                .fromFormData("scope", String.join(" ", accessScopes.getScopes()))
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("grant_type", "client_credentials");

        AccessToken token = webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .block();
        log.trace("OAuth2 access token hentet.");
        return token;
    }
}