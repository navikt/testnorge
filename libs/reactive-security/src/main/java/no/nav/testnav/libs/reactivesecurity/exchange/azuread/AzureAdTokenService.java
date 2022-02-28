package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureNavClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.aad.issuer-uri")
public class AzureAdTokenService implements TokenService {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final GetAuthenticatedToken getAuthenticatedToken;

    public AzureAdTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            AzureNavClientCredential azureNavClientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        log.info("Init AzureAd token exchange.");

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
        this.webClient = builder.build();
        this.getAuthenticatedToken = getAuthenticatedToken;
        this.clientCredential = azureNavClientCredential;
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return getAuthenticatedToken
                .call()
                .flatMap(token -> {
                    if (token.getExpiredAt().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC))) {
                        log.warn("AccessToken har expired! Tokenet gikk ut: {}", token.getExpiredAt());
                        return Mono.error(new AccessDeniedException("Access token har utloept"));
                    }
                    if (token.isClientCredentials()) {
                        return generateClientCredentialAccessToken(serverProperties);
                    }
                    return generateOnBehalfOfAccessToken(token, serverProperties);
                });
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

    private Mono<AccessToken> generateClientCredentialAccessToken(ServerProperties serverProperties) {
        return new ClientCredentialExchangeCommand(
                webClient,
                clientCredential,
                serverProperties.toAzureAdScope()
        ).call();
    }

    private Mono<AccessToken> generateOnBehalfOfAccessToken(Token token, ServerProperties serverProperties) {
        return new OnBehalfOfExchangeCommand(
                webClient,
                clientCredential,
                serverProperties.toAzureAdScope(),
                token
        ).call();
    }
}
