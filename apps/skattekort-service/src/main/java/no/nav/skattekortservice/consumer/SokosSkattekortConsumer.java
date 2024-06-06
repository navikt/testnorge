package no.nav.skattekortservice.consumer;

import no.nav.skattekortservice.config.Consumers;
import no.nav.skattekortservice.consumer.command.SokosPostCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SokosSkattekortConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public SokosSkattekortConsumer(TokenExchange tokenExchange, Consumers consumers) {

        this.serverProperties = consumers.getSokosSkattekort();
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Mono<String> sendSkattekort(String skattekort) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new SokosPostCommand(webClient, skattekort, token.getTokenValue()).call());
    }
}
