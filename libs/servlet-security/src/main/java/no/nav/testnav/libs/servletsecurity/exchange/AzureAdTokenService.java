package no.nav.testnav.libs.servletsecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.time.Duration;

import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.GetWellKnownCommand;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureNavClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.WellKnown;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.servletsecurity.domain.ResourceServerType;

@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.aad.issuer-uri")
public class AzureAdTokenService implements TokenService {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final GetAuthenticatedToken getAuthenticatedToken;
    private final Mono<WellKnown> wellKnown;

    public AzureAdTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${spring.security.oauth2.resourceserver.aad.issuer-uri}") String issuerUrl,
            AzureNavClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        log.info("Init AzureAd token exchange.");
        this.getAuthenticatedToken = getAuthenticatedToken;
        WebClient.Builder builder = WebClient.builder();

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
        this.clientCredential = clientCredential;
        this.wellKnown = new GetWellKnownCommand(this.webClient, issuerUrl).call().cache(
                value -> Duration.ofDays(7),
                value -> Duration.ZERO,
                () -> Duration.ZERO
        );
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        var token = getAuthenticatedToken.call();

        if (token.isClientCredentials()) {
            return generateClientCredentialAccessToken(serverProperties);
        }
        return generateOnBehalfOfAccessToken(token, serverProperties);
    }

    private Mono<AccessToken> generateClientCredentialAccessToken(ServerProperties serverProperties) {
        return new ClientCredentialExchangeCommand(webClient, clientCredential, serverProperties.toAzureAdScope(), wellKnown).call();

    }

    private Mono<AccessToken> generateOnBehalfOfAccessToken(Token token, ServerProperties serverProperties) {
        return new OnBehalfOfExchangeCommand(webClient, clientCredential, serverProperties.toAzureAdScope(), token, wellKnown).call();
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }
}
