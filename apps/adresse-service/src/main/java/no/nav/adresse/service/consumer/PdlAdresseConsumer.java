package no.nav.adresse.service.consumer;

import no.nav.adresse.service.config.credentials.PdlServiceProperties;
import no.nav.adresse.service.consumer.command.PdlAdresseSoekCommand;
import no.nav.adresse.service.dto.GraphQLRequest;
import no.nav.adresse.service.dto.PdlAdresseResponse;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
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
