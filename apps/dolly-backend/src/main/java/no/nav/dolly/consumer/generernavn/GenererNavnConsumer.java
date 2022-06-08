package no.nav.dolly.consumer.generernavn;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.config.credentials.GenererNavnServiceProperties;
import no.nav.dolly.consumer.generernavn.command.GenererNavnCommand;
import no.nav.dolly.domain.PdlPerson.Navn;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Component
public class GenererNavnConsumer {


    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
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


        ResponseEntity<JsonNode> response =
                new GenererNavnCommand(webClient, serviceProperties.getAccessToken(tokenService), antall, getNavCallId()).call().block();

        if (!response.hasBody()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().body(mapper.mapAsList(response.getBody(), Navn.class));

    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
