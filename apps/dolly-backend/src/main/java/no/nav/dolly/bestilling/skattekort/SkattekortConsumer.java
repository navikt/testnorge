package no.nav.dolly.bestilling.skattekort;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.skattekort.command.SkattekortPostCommand;
import no.nav.dolly.bestilling.skattekort.domain.OpprettSkattekortRequest;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class SkattekortConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public SkattekortConsumer(
            WebClient webClient,
            Consumers consumers,
            TokenExchange tokenExchange) {

        this.serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Flux<String> sendSkattekort(OpprettSkattekortRequest skattekortRequest) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new SkattekortPostCommand(webClient, skattekortRequest, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Skattekort sendt med response: {}", response));
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-dolly-proxy";
    }
}
