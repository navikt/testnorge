package no.nav.testnav.apps.tenorsearchservice.consumers;

import no.nav.testnav.apps.tenorsearchservice.config.Consumers;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.FinnesIDollyGetCommand;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyBackendSelector;
import no.nav.testnav.libs.dto.dolly.v1.FinnesDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyBackendSelector.REGULAR;

@Service
public class DollyBackendConsumer {

    private final WebClient webClient;
    private final WebClient webClientDev;
    private final TokenExchange tokenExchange;
    private final ServerProperties dollyBackendProperties;
    private final ServerProperties dollyBackendPropertiesDev;

    public DollyBackendConsumer(TokenExchange tokenExchange, Consumers serverProperties,
                                WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getDollyBackend().getUrl())
                .build();
        this.webClientDev = webClientBuilder
                .baseUrl(serverProperties.getDollyBackendDev().getUrl())
                .build();
        this.tokenExchange = tokenExchange;
        this.dollyBackendProperties = serverProperties.getDollyBackend();
        this.dollyBackendPropertiesDev = serverProperties.getDollyBackendDev();
    }

    public Mono<FinnesDTO> getFinnesInfo(List<String> identer, DollyBackendSelector selector) {

        var properties = selector == REGULAR ? dollyBackendProperties : dollyBackendPropertiesDev;
        var client = selector == REGULAR ? webClient : webClientDev;
        return tokenExchange.exchange(properties)
                .flatMap(token -> new FinnesIDollyGetCommand(client, identer, token.getTokenValue()).call());
    }
}
