package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.credentials.GenererNavnServiceProperties;
import no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GenererNavnConsumer {

    private final WebClient webClient;
    private final GenererNavnServiceProperties properties;
    private final TokenExchange tokenExchange;

    public GenererNavnConsumer(GenererNavnServiceProperties properties,
                               TokenExchange tokenExchange) {

        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public NavnDTO genereNavn() {
        var accessToken = tokenExchange.exchange(properties).block();
        GenererNavnCommand command = new GenererNavnCommand(webClient, accessToken.getTokenValue(), 1);
        return command.call()[0];
    }
}
