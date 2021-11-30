package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

import no.nav.testnav.libs.reactivesecurity.domain.AzureTrygdeetatenClientCredential;
import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ClientCredential;
import no.nav.testnav.libs.reactivesecurity.exchange.GenerateToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Slf4j
@Service
public class TrygdeetatenAzureAdTokenService implements GenerateToken {

    private final WebClient webClient;
    private final ClientCredential clientCredential;

    public TrygdeetatenAzureAdTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
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

    }

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return new ClientCredentialExchangeCommand(
                webClient,
                clientCredential,
                serverProperties
        ).call();

    }
}
