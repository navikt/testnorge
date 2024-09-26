package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.config.Consumers;
import no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GenererNavnConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public GenererNavnConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getGenererNavnService();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public NavnDTO genereNavn() {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        GenererNavnCommand command = new GenererNavnCommand(webClient, accessToken.getTokenValue(), 1);
        return command.call()[0];
    }
}
