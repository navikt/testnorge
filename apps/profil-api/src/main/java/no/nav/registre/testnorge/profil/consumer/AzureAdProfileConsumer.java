package no.nav.registre.testnorge.profil.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.OnBehalfOfGenerateAccessTokenService;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileCommand;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileImageCommand;
import no.nav.registre.testnorge.profil.consumer.credentials.ProfilApiClientCredential;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;
import no.nav.registre.testnorge.profil.domain.Profil;

@Slf4j
@Component
public class AzureAdProfileConsumer {

    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final OnBehalfOfGenerateAccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public AzureAdProfileConsumer(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${api.azuread.url}") String url,
            ProfilApiClientCredential clientCredential,
            OnBehalfOfGenerateAccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes(url + "/.default");
        this.clientCredential = clientCredential;
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
        this.webClient = builder.baseUrl(url).build();
    }

    public Profil getProfil() {
        AccessToken accessToken = accessTokenService.generateToken(clientCredential, accessScopes);
        ProfileDTO dto = new GetProfileCommand(webClient, accessToken.getTokenValue()).call();
        return new Profil(dto);
    }

    public byte[] getProfilImage() {
        AccessToken accessToken = accessTokenService.generateToken(clientCredential, accessScopes);
        ByteArrayResource resource = new GetProfileImageCommand(webClient, accessToken.getTokenValue()).call();
        return resource.getByteArray();
    }

}