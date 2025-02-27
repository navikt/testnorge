package no.nav.dolly.consumer.brukerservice;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.consumer.brukerservice.command.BrukerServiceGetTilgangCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class BrukerServiceConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public BrukerServiceConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder) {

        this.tokenService = tokenService;
        serverProperties = consumers.getBrukerService();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<String> getKollegaerIOrganisasjon(String brukerId) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new BrukerServiceGetTilgangCommand(webClient, brukerId, token.getTokenValue()).call());
    }
}
