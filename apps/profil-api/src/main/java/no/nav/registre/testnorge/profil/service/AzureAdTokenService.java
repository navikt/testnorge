package no.nav.registre.testnorge.profil.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
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
public class AzureAdTokenService {

    private final WebClient webClient;
    private final AzureClientCredential clientCredential;
    private final GetAuthenticatedToken getAuthenticatedToken;

    AzureAdTokenService(
            WebClient webClient,
            @Value("${HTTP_PROXY:#{null}}") String proxyHost,
            AzureClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        log.info("Init custom AzureAd token exchange.");
        this.getAuthenticatedToken = getAuthenticatedToken;
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
        this.clientCredential = clientCredential;
    }

    public Mono<AccessToken> exchange(String scope) {
        return new OnBehalfOfExchangeCommand(webClient, clientCredential, scope, getAuthenticatedToken.call()).call();
    }

}
