package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.GenererNavnServiceProperties;
import no.nav.pdl.forvalter.consumer.command.GenererNavnServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VerifiserNavnServiceCommand;
import no.nav.pdl.forvalter.metrics.Timed;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class GenererNavnServiceConsumer {

    private static final String NAVN_URL = "/api/v1/navn";
    private static final String NAVN_CHECK_URL = NAVN_URL + "/check";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public GenererNavnServiceConsumer(TokenExchange tokenExchange, GenererNavnServiceProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "genererNavnService_getNavn"})
    public Optional<NavnDTO> getNavn(Integer antall) {

        return Arrays.asList(tokenExchange.exchange(properties).flatMap(
                                token -> new GenererNavnServiceCommand(webClient, NAVN_URL, antall, token.getTokenValue()).call())
                        .block())
                .stream().findFirst();
    }

    @Timed(name = "providers", tags = {"operation", "genererNavnService_veriyfNavn"})
    public Boolean verifyNavn(NavnDTO navn) {

        return tokenExchange.exchange(properties).flatMap(
                        token -> new VerifiserNavnServiceCommand(webClient, NAVN_CHECK_URL, navn, token.getTokenValue()).call())
                .block();
    }
}
