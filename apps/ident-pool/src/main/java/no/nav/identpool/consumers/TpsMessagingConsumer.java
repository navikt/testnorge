package no.nav.identpool.consumers;

import no.nav.identpool.config.credentials.TpsMessagingServiceProperties;
import no.nav.identpool.consumers.command.TpsMessagingGetCommand;
import no.nav.identpool.dto.TpsIdentStatusDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class TpsMessagingConsumer {

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public TpsMessagingConsumer(
            TpsMessagingServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public List<TpsIdentStatusDTO> getIdentStatuser(List<String> identer, boolean includeProd) {

        return tokenExchange.exchange(serviceProperties)
                .flatMapMany(token -> new TpsMessagingGetCommand(webClient, token.getTokenValue(), identer, includeProd).call())
                .collectList()
                .block();
    }
}
