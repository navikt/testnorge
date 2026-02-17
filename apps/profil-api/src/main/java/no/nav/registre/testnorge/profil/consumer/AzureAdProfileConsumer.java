package no.nav.registre.testnorge.profil.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileCommand;
import no.nav.registre.testnorge.profil.consumer.command.GetProfileImageCommand;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.registre.testnorge.profil.service.AzureOnBehalfOfTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AzureAdProfileConsumer {

    private final WebClient webClient;
    private final AzureOnBehalfOfTokenService azureAdTokenService;

    private final String url;

    public AzureAdProfileConsumer(
            @Value("${api.azuread.url}") String url,
            AzureOnBehalfOfTokenService azureAdTokenService,
            WebClient webClient
    ) {
        this.url = url;
        this.azureAdTokenService = azureAdTokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(url)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    public Mono<Profil> getProfil() {
        return azureAdTokenService.exchange(url + "/.default")
                .flatMap(accessToken -> new GetProfileCommand(webClient, accessToken.getTokenValue()).call())
                .map(Profil::new);
    }

    public Mono<byte[]> getProfilImage() {
        return azureAdTokenService.exchange(url + "/.default")
                .flatMap(accessToken -> new GetProfileImageCommand(webClient, accessToken.getTokenValue()).call())
                .onErrorResume(e -> {
                    log.warn("Finner ikke profilbilde", e);
                    return Mono.empty();
                });
    }

}