package no.nav.dolly.bestilling.udistub;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.udistub.command.UdistubDeleteCommand;
import no.nav.dolly.bestilling.udistub.command.UdistubGetCommand;
import no.nav.dolly.bestilling.udistub.command.UdistubPostCommand;
import no.nav.dolly.bestilling.udistub.command.UdistubPutCommand;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.config.credentials.UdistubServerProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.JacksonExchangeStrategyUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class UdiStubConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public UdiStubConsumer(
            TokenExchange accessTokenService,
            UdistubServerProperties serverProperties,
            ObjectMapper objectMapper) {

        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "udi_getPerson"})
    public Mono<UdiPersonResponse> getUdiPerson(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new UdistubGetCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "udi_createPerson"})
    public Mono<UdiPersonResponse> createUdiPerson(UdiPerson udiPerson) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new UdistubPostCommand(webClient, udiPerson, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "udi_updatePerson"})
    public Mono<UdiPersonResponse> updateUdiPerson(UdiPerson udiPerson) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new UdistubPutCommand(webClient, udiPerson, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "udi_deletePerson"})
    public Mono<List<Void>> deleteUdiPerson(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .map(index -> new UdistubDeleteCommand(webClient,
                                identer.get(index), token.getTokenValue()).call())
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
        return "testnav-udistub-proxy";
    }
}
