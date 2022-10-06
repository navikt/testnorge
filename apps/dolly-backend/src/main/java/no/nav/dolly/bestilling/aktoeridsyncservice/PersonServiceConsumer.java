package no.nav.dolly.bestilling.aktoeridsyncservice;

import no.nav.dolly.bestilling.aktoeridsyncservice.command.PersonServiceExistCommand;
import no.nav.dolly.bestilling.aktoeridsyncservice.domain.AktoerIdent;
import no.nav.dolly.config.credentials.PersonServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Service
public class PersonServiceConsumer {

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

    @Timed(name = "providers", tags = {"operation", "personService_exists"})
    public Boolean isPerson(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token ->
                new PersonServiceExistCommand(webClient, ident, token.getTokenValue()).call())
                        .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
