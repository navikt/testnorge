package no.nav.dolly.bestilling.skattekort;

import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.skattekort.command.SkattekortPostCommand;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class SkattekortConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public SkattekortConsumer(
            WebClient webClient,
            Consumers consumers,
            TokenExchange tokenExchange) {

        this.serverProperties = consumers.getTestnavSkattekortService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Flux<String> sendSkattekort(SkattekortRequestDTO skattekortRequestDTO) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new SkattekortPostCommand(webClient, skattekortRequestDTO, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-skattekort-service";
    }
}
