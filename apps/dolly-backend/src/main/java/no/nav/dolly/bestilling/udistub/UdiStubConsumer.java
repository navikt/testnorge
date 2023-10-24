package no.nav.dolly.bestilling.udistub;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.udistub.command.UdistubDeleteCommand;
import no.nav.dolly.bestilling.udistub.command.UdistubGetCommand;
import no.nav.dolly.bestilling.udistub.command.UdistubPostCommand;
import no.nav.dolly.bestilling.udistub.command.UdistubPutCommand;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.JacksonExchangeStrategyUtil;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UdiStubConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public UdiStubConsumer(
            TokenExchange accessTokenService,
            Consumers.UdistubServer serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
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
    public Flux<UdiPersonResponse> deleteUdiPerson(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(identer)
                        .map(ident -> new UdistubDeleteCommand(webClient,
                                ident, token.getTokenValue()).call())
                        .flatMap(Flux::from));
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
