package no.nav.registre.testnorge.profil.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileCommand;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileImageCommand;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.registre.testnorge.profil.service.AzureAdTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
public class AzureAdProfileConsumer {

    private final WebClient webClient;
    private final AzureAdTokenService azureAdTokenService;

    private final String url;

    public AzureAdProfileConsumer(
            @Value("${HTTP_PROXY:#{null}}") String proxyHost,
            @Value("${api.azuread.url}") String url,
            AzureAdTokenService azureAdTokenService,
            WebClient webClient
    ) {
        this.url = url;
        this.azureAdTokenService = azureAdTokenService;
        var builder = webClient
                .mutate()
                .baseUrl(url)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build());
        if (proxyHost != null) {
            log.trace("Setter opp proxy host {} for Azure Ad", proxyHost);
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

    public Mono<Profil> getProfil() {
        return azureAdTokenService.exchange(url + "/.default")
                .flatMap(accessToken -> new GetProfileCommand(webClient, accessToken.getTokenValue()).call())
                .map(Profil::new);
    }

    public Optional<byte[]> getProfilImage() {
        try {
            return Optional.ofNullable(azureAdTokenService.exchange(url + "/.default")
                    .flatMap(accessToken -> new GetProfileImageCommand(webClient, accessToken.getTokenValue()).call())
                    .block());
        } catch (IllegalStateException e) {
            log.warn("Finner ikke profilbilde", e);
            return Optional.empty();
        }
    }

}