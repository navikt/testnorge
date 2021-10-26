package no.nav.dolly.security.oauth2.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.security.domain.DollyBackendClientCredential;
import no.nav.dolly.security.oauth2.domain.AccessScopes;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.domain.ClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

import static no.nav.dolly.util.CurrentAuthentication.getJwtToken;


@Slf4j
@Service
class OnBehalfOfGenerateAccessTokenService {
    private final WebClient webClient;
    private final ClientCredential clientCredential;

    public OnBehalfOfGenerateAccessTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            DollyBackendClientCredential clientCredential
    ) {
        this.clientCredential = clientCredential;

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

        this.webClient = builder.build();
    }

    public AccessToken generateToken(AccessScopes accessScopes) {
        if (accessScopes.getScopes().isEmpty()) {
            throw new DollyFunctionalException("Kan ikke opprette accessToken uten clients");
        }
        String jwtToken = getJwtToken();

        var body = BodyInserters
                .fromFormData("scope", String.join(" ", accessScopes.getScopes()))
                .with("client_id", clientCredential.getClientId())
                .with("client_secret", clientCredential.getClientSecret())
                .with("assertion", jwtToken)
                .with("requested_token_use", "on_behalf_of")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        AccessToken token = webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .block();

        log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow");
        return token;
    }
}