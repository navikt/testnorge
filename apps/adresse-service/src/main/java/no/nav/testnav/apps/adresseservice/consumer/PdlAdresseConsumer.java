package no.nav.testnav.apps.adresseservice.consumer;

import no.nav.testnav.apps.adresseservice.config.credentials.PdlServiceProperties;
import no.nav.testnav.apps.adresseservice.consumer.command.PdlAdresseSoekCommand;
import no.nav.testnav.apps.adresseservice.dto.GraphQLRequest;
import no.nav.testnav.apps.adresseservice.dto.PdlAdresseResponse;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PdlAdresseConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public PdlAdresseConsumer(TokenExchange tokenExchange, PdlServiceProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public PdlAdresseResponse sendAdressesoek(GraphQLRequest adresseQuery) {
        return tokenExchange.generateToken(properties)
                .flatMap(token -> new PdlAdresseSoekCommand(webClient, adresseQuery, token.getTokenValue()).call())
                .block();
    }

}
