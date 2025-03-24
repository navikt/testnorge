package no.nav.skattekortservice.consumer;

import no.nav.skattekortservice.config.Consumers;
import no.nav.skattekortservice.consumer.command.SokosGetCommand;
import no.nav.skattekortservice.consumer.command.SokosPostCommand;
import no.nav.skattekortservice.dto.SokosRequest;
import no.nav.skattekortservice.dto.SokosResponse;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SokosSkattekortConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public SokosSkattekortConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient
    ) {
        this.serverProperties = consumers.getSokosSkattekort();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Mono<String> sendSkattekort(SokosRequest request) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new SokosPostCommand(webClient, request, token.getTokenValue()).call());
    }

    public Flux<SokosResponse> hentSkattekort(String ident) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new SokosGetCommand(webClient, ident,
                        token.getTokenValue()).call());
    }
}
