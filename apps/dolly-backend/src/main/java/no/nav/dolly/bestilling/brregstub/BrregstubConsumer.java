package no.nav.dolly.bestilling.brregstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.brregstub.command.BrregDeleteCommand;
import no.nav.dolly.bestilling.brregstub.command.BrregGetCommand;
import no.nav.dolly.bestilling.brregstub.command.BrregPostCommand;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class BrregstubConsumer extends ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public BrregstubConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<RolleoversiktTo> getRolleoversikt(String ident) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new BrregGetCommand(webClient, ident, token.getTokenValue()).call());
    }

    public Mono<RolleoversiktTo> postRolleoversikt(RolleoversiktTo rolleoversiktTo) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new BrregPostCommand(webClient, rolleoversiktTo, token.getTokenValue()).call());
    }

    public Mono<List<Void>> deleteRolleoversikt(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .map(idx -> new BrregDeleteCommand(webClient, identer.get(idx), token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
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
