package no.nav.registre.testnorge.tilbakemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.profil.v1.ProfilDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
@DependencyOn("testnorge-profil-api")
public class ProfilApiConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public ProfilApiConsumer(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${consumer.profil-api.url}") String url,
            @Value("${consumer.profil-api.client-id}") String clientId,
            AccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
        this.accessTokenService = accessTokenService;

        WebClient.Builder builder = WebClient.builder();
        if (proxyHost != null) {
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
        this.webClient = builder
                .baseUrl(url)
                .build();
    }

    public ProfilDTO getBruker() {
        AccessToken accessToken = accessTokenService.generateToken(accessScopes);
        log.info("Henter bruker fra Azure.");
        return webClient.get()
                .uri("/api/v1/profil")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(ProfilDTO.class)
                .block();
    }
}