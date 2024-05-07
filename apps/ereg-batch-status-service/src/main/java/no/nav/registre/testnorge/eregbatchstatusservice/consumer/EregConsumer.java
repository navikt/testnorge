package no.nav.registre.testnorge.eregbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.eregbatchstatusservice.config.Consumers;
import no.nav.registre.testnorge.eregbatchstatusservice.consumer.command.GetBatchStatusCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class EregConsumer {
    private final ServerProperties serverProperties;
    private final WebClient webClient;
    private final TokenExchange tokenService;


    public EregConsumer(TokenExchange tokenService,
                        Consumers consumers
    ) {

        this.tokenService = tokenService;
        this.serverProperties = consumers.getModappEregProxy();
        this.webClient = WebClient.builder()
                .baseUrl(consumers.getModappEregProxy().getUrl())
                .build();
    }

    public Mono<Long> getStatusKode(String miljo, Long id) {
        return tokenService
                .exchange(serverProperties)
                .flatMap(accessToken ->
                        new GetBatchStatusCommand(webClient, miljo, id, accessToken.getTokenValue()).call());
    }
}