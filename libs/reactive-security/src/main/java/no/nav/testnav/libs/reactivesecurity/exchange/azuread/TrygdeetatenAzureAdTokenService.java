package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.time.Duration;

import no.nav.testnav.libs.reactivesecurity.domain.AzureTrygdeetatenClientCredential;
import no.nav.testnav.libs.reactivesecurity.exchange.ExchangeToken;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.GetWellKnownCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.WellKnown;

@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.aad.issuer-uri")
public class TrygdeetatenAzureAdTokenService implements ExchangeToken {

    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final Mono<WellKnown> wellKnown;

    public TrygdeetatenAzureAdTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${spring.security.oauth2.resourceserver.aad.issuer-uri}") String issuerUrl,
            AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredential
    ) {
        this.clientCredential = azureTrygdeetatenClientCredential;
        log.info("Init AzureAd Trygdeetaten token service.");
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(azureTrygdeetatenClientCredential.getTokenEndpoint())
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

        this.wellKnown = new GetWellKnownCommand(this.webClient, issuerUrl).call().cache(
                value -> Duration.ofDays(7),
                value -> Duration.ZERO,
                () -> Duration.ZERO
        );

    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return new ClientCredentialExchangeCommand(
                webClient,
                clientCredential,
                serverProperties.toAzureAdScope(),
                wellKnown
        ).call();

    }
}
