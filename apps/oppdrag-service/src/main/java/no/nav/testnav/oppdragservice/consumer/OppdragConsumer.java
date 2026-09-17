package no.nav.testnav.oppdragservice.consumer;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.oppdragservice.config.Consumers;
import no.nav.testnav.oppdragservice.consumer.command.OppdragPostComand;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

//@Service
public class OppdragConsumer {

    private final ServerProperties serverProperties;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;

    public OppdragConsumer(Consumers consumers,
                           WebClient.Builder webClientBuilder,
                           TokenExchange tokenExchange) {

        this.serverProperties = consumers.getOppdragProxy();
        this.webClient = webClientBuilder.baseUrl(serverProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public Mono<String> sendOppdrag(String miljoe, String melding) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new OppdragPostComand(webClient,
                        token.getTokenValue(), miljoe, melding).call());
    }
}
