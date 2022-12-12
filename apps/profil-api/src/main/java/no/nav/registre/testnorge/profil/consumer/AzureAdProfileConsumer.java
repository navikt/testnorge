package no.nav.registre.testnorge.profil.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileCommand;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileImageCommand;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.registre.testnorge.profil.service.AzureAdTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Component
public class AzureAdProfileConsumer {
    private final WebClient webClient;
    private final AzureAdTokenService azureAdTokenService;

    private final String url;

    public AzureAdProfileConsumer(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${api.azuread.url}") String url,
            AzureAdTokenService azureAdTokenService,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.url = url;
        this.azureAdTokenService = azureAdTokenService;
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(url)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build());

        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Azure Ad", proxyHost);
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
        this.webClient = builder
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Profil getProfil() {
        return azureAdTokenService.exchange(url + "/.default")
                .flatMap(accessToken -> new GetProfileCommand(webClient, accessToken.getTokenValue()).call())
                .map(Profil::new).block();
    }

    public Optional<byte[]> getProfilImage() {
        try {
            return Optional.ofNullable(azureAdTokenService.exchange(url + "/.default")
                    .flatMap(accessToken -> new GetProfileImageCommand(webClient, accessToken.getTokenValue()).call())
                    .block());
        } catch (IllegalStateException e) {
            log.warn("Finner ikke profil bilde", e);
            return Optional.empty();
        }
    }

}