package no.nav.registre.testnorge.profil.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileCommand;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileImageCommand;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;
import no.nav.registre.testnorge.profil.domain.Profil;

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

    public Profil getProfil() {
        AccessToken accessToken = accessTokenService.generateToken(accessScopes).block();
        ProfileDTO dto = new GetProfileCommand(webClient, accessToken.getTokenValue()).call();
        return new Profil(dto);
    }

    public byte[] getProfilImage() {
        AccessToken accessToken = accessTokenService.generateToken(accessScopes).block();
        return new GetProfileImageCommand(webClient, accessToken.getTokenValue()).call();
    }

}