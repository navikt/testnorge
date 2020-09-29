package no.nav.registre.testnorge.libs.oauth2.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;
import java.util.Map;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Slf4j
@Service
public class OnBehalfOfGenerateAccessTokenService {
    private final WebClient webClient;
    private final AuthenticationTokenResolver tokenResolver;

    public OnBehalfOfGenerateAccessTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            SecureAuthenticationTokenResolver tokenResolver
    ) {

        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(issuerUrl + "/oauth2/v2.0/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for OAuth 2.0 On-Behalf-Of Flow", proxyHost);
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

    public AccessToken generateToken(ClientCredential clientCredential, AccessScopes accessScopes) {
        if (tokenResolver.isClientCredentials()) {
            throw new BadCredentialsException("Kan ikke gjennomfore On Behalf of fra Client Credentials.");
        }
        if (accessScopes.getScopes().isEmpty()) {
            throw new RuntimeException("Kan ikke opprette accessToken uten clients");
        }
        tokenResolver.verifyAuthentication();

        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        contextMap.put("oid", tokenResolver.getOid());
        MDC.setContextMap(contextMap);

        JwtAuthenticationToken jwtAuthenticationToken = tokenResolver.jwtAuthenticationToken();

        var body = BodyInserters
                .fromFormData("scope", String.join(" ", accessScopes.getScopes()))
                .with("client_id", clientCredential.getClientId())
                .with("client_secret", clientCredential.getClientSecret())
                .with("assertion", jwtAuthenticationToken.getToken().getTokenValue())
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
