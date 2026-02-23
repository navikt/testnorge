package no.nav.registre.sdforvalter.consumer.rs.navn;

import no.nav.registre.sdforvalter.config.Consumers;
import no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GenererNavnConsumer {

    private final WebClient webClient;
    private final ServerProperties properties;
    private final TokenExchange tokenExchange;

    public GenererNavnConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        properties = consumers.getGenererNavnService();
        this.webClient = webClient
                .mutate()
                .baseUrl(properties.getUrl())
                .build();
    }

    public NavnDTO genereNavn() {
        var accessToken = tokenExchange.exchange(properties).block();
        GenererNavnCommand command = new GenererNavnCommand(webClient, accessToken.getTokenValue(), 1);
        return command.call()[0];
    }
}
