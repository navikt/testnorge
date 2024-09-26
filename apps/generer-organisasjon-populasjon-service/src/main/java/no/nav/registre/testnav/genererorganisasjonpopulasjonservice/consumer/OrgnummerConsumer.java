package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.config.Consumers;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer.command.GetOrgnummerCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrgnummerConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public OrgnummerConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavOrgnummerService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public String getOrgnummer() {
        var tokenValue = tokenExchange.exchange(serverProperties).block().getTokenValue();
        return new GetOrgnummerCommand(webClient, tokenValue, 1).call().stream().findFirst().orElseThrow();
    }

}