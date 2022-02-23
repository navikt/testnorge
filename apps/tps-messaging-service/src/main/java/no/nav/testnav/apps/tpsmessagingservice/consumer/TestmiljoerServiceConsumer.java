package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.config.credentials.TestmiljoerServiceProperties;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TestmiljoerServiceCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class TestmiljoerServiceConsumer {

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public TestmiljoerServiceConsumer(
            TestmiljoerServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public List<String> getMiljoer() {

        return Arrays.asList(tokenExchange.exchange(serviceProperties)
                .flatMap(token -> new TestmiljoerServiceCommand(webClient, token.getTokenValue()).call())
                .block());
    }
}
