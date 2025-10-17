package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.time.ZonedDateTime;

import static java.util.Objects.isNull;

@Slf4j
public class AzureTokenService implements TokenService {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final GetAuthenticatedToken getAuthenticatedToken;

    public AzureTokenService(
            WebClient webClient,
            String proxyHost,
            AzureClientCredential azureClientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        log.info("Init AzureAd token exchange.");

        this.getAuthenticatedToken = getAuthenticatedToken;
        this.clientCredential = azureClientCredential;
        var builder = webClient
                .mutate()
                .baseUrl(azureClientCredential.getTokenEndpoint())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        if (proxyHost != null) {
            log.trace("Setter opp proxy host {} for Client Credentials", proxyHost);
            var uri = URI.create(proxyHost);
            builder.clientConnector(new ReactorClientHttpConnector(
                    HttpClient
                            .create()
                            .proxy(proxy -> proxy
                                    .type(ProxyProvider.Proxy.HTTP)
                                    .host(uri.getHost())
                                    .port(uri.getPort()))
            ));
        }
        this.webClient = builder.build();
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return getAuthenticatedToken
                .call()
                .flatMap(token -> {
                    if (isNull(token) || token.getExpiresAt().isBefore(ZonedDateTime.now().toInstant().plusSeconds(120))) {
                        log.warn("AccessToken har utløpt eller utløper innen kort tid!");
                        return Mono.error(new AccessDeniedException("Access token har utløpt eller utløper innen kort tid"));
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

    public static class Test extends AzureTokenService {

        public Test(WebClient webClient, String proxyHost, AzureClientCredential azureClientCredential, GetAuthenticatedToken getAuthenticatedToken) {
            super(webClient, proxyHost, azureClientCredential, getAuthenticatedToken);
        }

        @Override
        public Mono<AccessToken> exchange(ServerProperties serverProperties) {
            return Mono.empty();
        }

    }

}
