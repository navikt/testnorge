package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.Consumers;
import no.nav.organisasjonforvalter.consumer.command.GenererNavnCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
public class GenererNavnServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public GenererNavnServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getGenererNavnService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public List<String> getOrgName(Integer antall) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new GenererNavnCommand(webClient, antall, token.getTokenValue()).call())
                .map(value -> format("%s %s", value.getAdjektiv(), value.getSubstantiv()))
                .collect(Collectors.toList())
                .block();
    }

    public String getOrgName() {

        List<String> orgName = getOrgName(1);
        return orgName.isEmpty() ? null : orgName.getFirst();
    }
}
