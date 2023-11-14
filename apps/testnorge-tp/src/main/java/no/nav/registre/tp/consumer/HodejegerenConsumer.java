package no.nav.registre.tp.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.config.Consumers;
import no.nav.registre.tp.consumer.command.GetLevendeIdenterCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Component
@Slf4j
public class HodejegerenConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public HodejegerenConsumer(
            Consumers consumers,
            TokenExchange tokenExchange
    ) {
        serverProperties = consumers.getHodejegeren();
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<String> getLevende(
            Long avspillergruppeId
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetLevendeIdenterCommand(avspillergruppeId, accessToken.getTokenValue(), webClient).call())
                .block();
    }

}
