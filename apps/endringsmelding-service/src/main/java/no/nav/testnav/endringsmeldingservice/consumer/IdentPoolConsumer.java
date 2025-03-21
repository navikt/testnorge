package no.nav.testnav.endringsmeldingservice.consumer;

import no.nav.testnav.endringsmeldingservice.config.Consumers;
import no.nav.testnav.endringsmeldingservice.consumer.command.IdentpoolPostCommand;
import no.nav.testnav.endringsmeldingservice.consumer.dto.HentIdenterRequest;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class IdentPoolConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public IdentPoolConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getIdentPool();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<List<String>> acquireIdents(HentIdenterRequest request) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new IdentpoolPostCommand(webClient, request,
                        token.getTokenValue()).call());
    }
}