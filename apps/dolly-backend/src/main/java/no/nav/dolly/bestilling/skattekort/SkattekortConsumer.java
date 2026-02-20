package no.nav.dolly.bestilling.skattekort;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.skattekort.command.SkattekortHentCommand;
import no.nav.dolly.bestilling.skattekort.command.SkattekortOpprettCommand;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortHentRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public Mono<SkattekortResponse> sendSkattekort(SkattekortRequest skattekortRequest) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new SkattekortOpprettCommand(webClient, skattekortRequest, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Skattekort sendt med response: {}", response));
    }

    public Mono<SkattekortResponse> hentSkattekort(SkattekortHentRequest request) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new SkattekortHentCommand(webClient, request, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Skattekort hentet med response: {}", response));
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
