package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oversiktfrontend.config.Consumers;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetBrukerCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetTokenCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.BrukerDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BrukerConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public BrukerConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavBrukerService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<BrukerDTO> getBruker(String orgnummer) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetBrukerCommand(webClient, accessToken.getTokenValue(), orgnummer).call());
    }

    public Mono<String> getToken(String id) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call());
    }

}
