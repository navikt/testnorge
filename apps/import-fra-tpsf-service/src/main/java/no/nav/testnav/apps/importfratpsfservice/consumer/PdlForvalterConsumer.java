package no.nav.testnav.apps.importfratpsfservice.consumer;

import no.nav.testnav.apps.importfratpsfservice.config.credentials.PdlForvalterProperties;
import no.nav.testnav.apps.importfratpsfservice.consumer.command.PdlForvalterPutCommand;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PdlForvalterConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public PdlForvalterConsumer(TokenExchange tokenExchange, PdlForvalterProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Mono<String> putPdlPerson(String ident, PersonDTO person) {

        return tokenExchange.exchange(properties).flatMap(
                token -> new PdlForvalterPutCommand(webClient, ident, person, token.getTokenValue()).call());
    }
}
