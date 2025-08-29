package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.tpsmessagingservice.command.MiljoerGetCommand;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class MiljoerConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public MiljoerConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavMiljoerService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<AccessToken> getToken() {

        return tokenService.exchange(serverProperties);
    }

    @Timed(name = "providers", tags = {"operation", "get_miljoer"})
    public Mono<List<String>> getMiljoer() {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new MiljoerGetCommand(webClient, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-miljoer-consumer";
    }
}
