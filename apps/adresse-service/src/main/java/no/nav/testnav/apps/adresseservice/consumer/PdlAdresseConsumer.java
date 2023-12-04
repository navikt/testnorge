package no.nav.testnav.apps.adresseservice.consumer;

import no.nav.testnav.apps.adresseservice.config.Consumers;
import no.nav.testnav.apps.adresseservice.consumer.command.PdlAdresseSoekCommand;
import no.nav.testnav.apps.adresseservice.dto.GraphQLRequest;
import no.nav.testnav.apps.adresseservice.dto.PdlAdresseResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PdlAdresseConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public PdlAdresseConsumer(
            TokenExchange tokenExchange,
            Consumers consumers) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getPdlServices();
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public PdlAdresseResponse sendAdressesoek(GraphQLRequest adresseQuery) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new PdlAdresseSoekCommand(webClient, adresseQuery, token.getTokenValue()).call())
                .block();
    }
}