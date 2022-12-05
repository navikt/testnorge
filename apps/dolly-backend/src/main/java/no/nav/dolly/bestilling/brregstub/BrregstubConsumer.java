package no.nav.dolly.bestilling.brregstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.brregstub.command.BrregDeleteCommand;
import no.nav.dolly.bestilling.brregstub.command.BrregGetCommand;
import no.nav.dolly.bestilling.brregstub.command.BrregPostCommand;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.config.credentials.BrregstubProxyProperties;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class BrregstubConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public BrregstubConsumer(TokenExchange tokenService,
                             BrregstubProxyProperties serverProperties,
                             ObjectMapper objectMapper,
                             ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<RolleoversiktTo> getRolleoversikt(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new BrregGetCommand(webClient, ident, token.getTokenValue()).call());
    }

    public Mono<RolleoversiktTo> postRolleoversikt(RolleoversiktTo rolleoversiktTo) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new BrregPostCommand(webClient, rolleoversiktTo, token.getTokenValue()).call());
    }

    public Mono<List<Void>> deleteRolleoversikt(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .map(idx -> new BrregDeleteCommand(webClient, identer.get(idx), token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-brregstub-proxy";
    }

}
