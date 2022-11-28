package no.nav.dolly.bestilling.personservice;

import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.personservice.command.PersonServiceExistCommand;
import no.nav.dolly.bestilling.personservice.command.PersonServiceSyncCommand;
import no.nav.dolly.config.credentials.PersonServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PersonServiceConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public PersonServiceConsumer(TokenExchange tokenService,
                                 PersonServiceProperties serverProperties,
                                 ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "personService_isPerson"})
    public Boolean isPerson(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new PersonServiceExistCommand(webClient, ident, token.getTokenValue()).call())
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    @Timed(name = "providers", tags = {"operation", "personService_pdlSyncReady"})
    public Mono<Boolean> getPdlSyncReady(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new PersonServiceSyncCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-person-service";
    }

}
