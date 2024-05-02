package no.nav.testnav.apps.tenorsearchservice.consumers;

import no.nav.testnav.apps.tenorsearchservice.config.Consumers;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.FinnesIDollyGetCommand;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.TagsGetCommand;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyBackendSelector;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagsDTO;
import no.nav.testnav.libs.dto.dolly.v1.FinnesDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.apache.hc.core5.reactor.Command;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyBackendSelector.DEV;

@Service
public class DollyBackendConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties dollyBackendProperties;
    private final ServerProperties dollyBackendDevProperties;

    public DollyBackendConsumer(TokenExchange tokenExchange, Consumers serverProperties) {
        this.webClient = WebClient.builder()
                .build();
        this.tokenExchange = tokenExchange;
        this.dollyBackendProperties = serverProperties.getDollyBackend();
        this.dollyBackendDevProperties = serverProperties.getDollyBackendDev();
    }

    public Mono<FinnesDTO> getFinnesInfo(List<String> identer, DollyBackendSelector selector) {

        var properties = selector == DEV ? dollyBackendProperties : dollyBackendDevProperties;
        return tokenExchange.exchange(properties)
                .flatMap(token -> new FinnesIDollyGetCommand(webClient, properties.getUrl(), identer, token.getTokenValue()).call());
    }
}
