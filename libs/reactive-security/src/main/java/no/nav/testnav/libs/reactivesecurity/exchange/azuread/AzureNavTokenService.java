package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureNavClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

@Slf4j
public class AzureNavTokenService implements TokenService {

    private final WebClient webClient;
    private final ClientCredential clientCredential;

    public AzureNavTokenService(
            String proxyHost,
            AzureNavClientCredential azureNavClientCredential
    ) {
        this.clientCredential = azureNavClientCredential;
        log.info("Init AzureAd Nav token service.");
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(azureNavClientCredential.getTokenEndpoint())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        if (proxyHost != null) {
            log.trace("Setter opp proxy host {} for Client Credentials", proxyHost);
            var uri = URI.create(proxyHost);
            HttpClient httpClient = HttpClient
                    .create()
                    .proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort()));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
        this.webClient = builder.build();

    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return new ClientCredentialExchangeCommand(
                webClient,
                clientCredential,
                serverProperties.toAzureAdScope()
        ).call();

    }

    public static class Test extends AzureNavTokenService {

        public Test(String proxyHost, AzureNavClientCredential azureNavClientCredential) {
            super(proxyHost, azureNavClientCredential);
        }

        @Override
        public Mono<AccessToken> exchange(ServerProperties serverProperties) {
            return Mono.empty();
        }

    }

}
