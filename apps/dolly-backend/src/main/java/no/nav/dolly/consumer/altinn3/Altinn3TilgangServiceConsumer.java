package no.nav.dolly.consumer.altinn3;

import no.nav.dolly.config.Consumers;
import no.nav.dolly.consumer.altinn3.command.Altinn3TilgangServiceGetCommand;
import no.nav.dolly.consumer.altinn3.dto.Altinn3TilgangDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class Altinn3TilgangServiceConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public Altinn3TilgangServiceConsumer(WebClient webClient,
                                         TokenExchange tokenExchange,
                                         Consumers consumers) {

         this.serverProperties = consumers.getAltinn3TilgangService();
         this.tokenService = tokenExchange;
         this.webClient = webClient
                 .mutate()
                 .baseUrl(serverProperties.getUrl())
                 .build();
    }

    public Flux<Altinn3TilgangDTO> getOrganisasjoner() {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new Altinn3TilgangServiceGetCommand(webClient, token.getTokenValue()).call());
    }
}
