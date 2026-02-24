package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.GenererNavnServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VerifiserNavnServiceCommand;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class GenererNavnServiceConsumer {

    private static final String NAVN_URL = "/api/v1/navn";
    private static final String NAVN_CHECK_URL = NAVN_URL + "/check";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public GenererNavnServiceConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getGenererNavnService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<NavnDTO> getNavn(Integer antall) {

        return Arrays.stream(tokenExchange.exchange(serverProperties)
                        .flatMap(token -> new GenererNavnServiceCommand(webClient, NAVN_URL, antall, token.getTokenValue()).call())
                        .block())
                .findFirst();
    }

    public Boolean verifyNavn(NavnDTO navn) {

        return tokenExchange.exchange(serverProperties).flatMap(
                        token -> new VerifiserNavnServiceCommand(webClient, NAVN_CHECK_URL, navn, token.getTokenValue()).call())
                .block();
    }
}
