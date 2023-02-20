package no.nav.dolly.consumer.generernavn;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.config.credentials.GenererNavnServiceProperties;
import no.nav.dolly.consumer.generernavn.command.GenererNavnCommand;
import no.nav.dolly.domain.PdlPerson.Navn;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Component
public class GenererNavnConsumer {


    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final MapperFacade mapper;

    public GenererNavnConsumer(TokenExchange tokenService,
                               GenererNavnServiceProperties serverProperties,
                               MapperFacade mapperFacade,
                               ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.mapper = mapperFacade;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public ResponseEntity<List<Navn>> getPersonnavn(Integer antall) {


        var response = tokenService.exchange(serviceProperties)
                .flatMap(token -> new GenererNavnCommand(webClient, token.getTokenValue(), antall, getNavCallId()).call())
                .block();

        if (!response.hasBody()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().body(mapper.mapAsList(response.getBody(), Navn.class));

    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
