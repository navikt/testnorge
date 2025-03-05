package no.nav.testnav.libs.standalone.servletsecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
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


@Slf4j
@Service
public class AzureAdTokenService implements ExchangeToken {

    private final WebClient webClient;
    private final ClientCredential clientCredential;

    AzureAdTokenService(
            WebClient webClient,
            @Value("${HTTP_PROXY:#{null}}") String proxyHost,
            AzureClientCredential clientCredential
    ) {
        log.info("Init AzureAd token exchange.");

        this.clientCredential = clientCredential;

        var builder = webClient
                .mutate()
                .baseUrl(clientCredential.getTokenEndpoint())
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
        return new ClientCredentialExchangeCommand(webClient, clientCredential, serverProperties.toAzureAdScope()).call();
    }

}
