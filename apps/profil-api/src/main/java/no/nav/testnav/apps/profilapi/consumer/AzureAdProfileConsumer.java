package no.nav.testnav.apps.profilapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.testnav.apps.profilapi.consumer.command.GetProfileCommand;
import no.nav.testnav.apps.profilapi.consumer.command.GetProfileImageCommand;
import no.nav.testnav.apps.profilapi.domain.Profil;
import no.nav.testnav.libs.reactivesecurity.domain.AccessScopes;
import no.nav.testnav.libs.reactivesecurity.service.AccessTokenService;

@Slf4j
@Component
public class AzureAdProfileConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public AzureAdProfileConsumer(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${api.azuread.url}") String url,
            AccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes(url + "/.default");
        this.accessTokenService = accessTokenService;

        WebClient.Builder builder = WebClient.builder();

        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Azure Ad", proxyHost);
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
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    public Mono<Profil> getProfil() {
        return accessTokenService
                .generateToken(accessScopes)
                .flatMap(accessToken -> new GetProfileCommand(webClient, accessToken.getTokenValue()).call())
                .map(Profil::new);
    }

    public Mono<byte[]> getProfilImage() {
        return accessTokenService
                .generateToken(accessScopes)
                .flatMap(accessToken -> new GetProfileImageCommand(webClient, accessToken.getTokenValue()).call());
    }

}